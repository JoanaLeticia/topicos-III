package br.unitins.foodflow.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.repository.ItemCardapioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ItemCardapioFileServiceImpl implements FileService {
    private final String PATH_UPLOAD = System.getProperty("user.home")
        + File.separator + "quarkus"
        + File.separator + "images"
        + File.separator + "itemcardapio" + File.separator;

    @Inject
    ItemCardapioRepository itemCardapioRepository;

    @Override
    @Transactional
    public void salvar(Long id, String nomeImagem, byte[] imagem) throws IOException {
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id);

        try {
            File diretorio = new File(PATH_UPLOAD);
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            if (itemCardapio.getNomeImagem() != null && !itemCardapio.getNomeImagem().isEmpty()) {
                File arquivoAntigo = new File(PATH_UPLOAD + itemCardapio.getNomeImagem());
                if (arquivoAntigo.exists()) {
                    arquivoAntigo.delete();
                }
            }

            String novoNome = gerarNomeUnico(nomeImagem);
            Path destino = Paths.get(PATH_UPLOAD + novoNome);
            Files.write(destino, imagem);

            itemCardapio.setNomeImagem(novoNome);
            itemCardapioRepository.persist(itemCardapio);

        } catch (IOException e) {
            throw new IOException("Erro ao salvar imagem do Item do Cardápio: " + e.getMessage(), e);
        }
    }

    private String gerarNomeUnico(String nomeOriginal) {
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        return UUID.randomUUID().toString() + extensao;
    }

    @Override
    public File download(String nomeArquivo) {
        File file = new File(PATH_UPLOAD + nomeArquivo);
        if (!file.exists()) {
            try (InputStream in = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("images/" + nomeArquivo)) {
                
                if (in != null) {
                    Files.copy(in, Paths.get(PATH_UPLOAD + nomeArquivo), StandardCopyOption.REPLACE_EXISTING);
                    return new File(PATH_UPLOAD + nomeArquivo);
                }
            } catch (IOException e) {
                return null;
            }
        }
        return file;
    }
}
