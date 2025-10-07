package br.unitins.foodflow.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.repository.ItemCardapioRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InitialDataService {

    @Inject
    ItemCardapioRepository itemCardapioRepository;

    @Inject
    FileService fileService;

    // Caminho relativo dentro de resources
    private static final String IMAGES_DIR = "images/";
    
    // Caminho absoluto para salvar as imagens processadas
    private final String PATH_UPLOAD = System.getProperty("user.home")
        + File.separator + "quarkus"
        + File.separator + "images"
        + File.separator + "itemcardapio" + File.separator;

    void onStart(@Observes StartupEvent ev) {
        try {
            assignImagesToProducts();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar imagens iniciais", e);
        }
    }

    @Transactional
    public void assignImagesToProducts() throws IOException {
        List<ItemCardapio> itemCardapios = itemCardapioRepository.listAll();
        
        // Criar diretório de upload se não existir
        Files.createDirectories(Paths.get(PATH_UPLOAD));
        
        for (ItemCardapio itemCardapio : itemCardapios) {
            if (itemCardapio.getNomeImagem() == null || itemCardapio.getNomeImagem().isEmpty()) {
                String imageName = getImageNameForItem(itemCardapio.getNome());
                
                // Carregar a imagem do resources
                try (InputStream in = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(IMAGES_DIR + imageName)) {
                    
                    if (in != null) {
                        // Salvar no diretório de upload
                        Path targetPath = Paths.get(PATH_UPLOAD + imageName);
                        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        
                        itemCardapio.setNomeImagem(imageName);
                        itemCardapioRepository.persist(itemCardapio);
                    }
                }
            }
        }
    }

    private String getImageNameForItem(String itemName) {
        // Implemente sua lógica de mapeamento de nomes
        return itemName.toLowerCase()
            .replace(" ", "-")
            .replace(":", "")
            .replace("'", "") + ".jpg";
    }
}
