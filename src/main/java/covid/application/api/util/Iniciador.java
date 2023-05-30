package covid.application.api.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iniciador {

    private static Logger LOG = LoggerFactory.getLogger(Iniciador.class);
    private static String arquivo = "config.properties";
    private static String os = System.getProperty("os.name").toUpperCase();

    public static void start() throws Exception {
        LOG.info("Verifica se está tudo configurado corretamente no sistema local");
        VerificadorSistema();
    }

    public static void VerificadorSistema() throws Exception {

        LOG.info("Irei verificar se banco está devidamente configurado");

        Properties properties = new Properties();

        try (InputStream inputStream = Iniciador.class.getClassLoader().getResourceAsStream(arquivo)) {
            properties.load(inputStream);
        } catch (Exception e) {
            LOG.error("Houve um erro ao ler o " + arquivo, e);
            throw e;
        }

        try {
            String valorPropriedade = properties.getProperty("sistema.operacional");
            if (valorPropriedade == null) {
                LOG.error("O valor da propriedade 'sistema.operacional' não foi encontrada no arquivo " + arquivo);
                throw new Exception(
                        "O valor da propriedade 'sistema.operacional' não foi encontrada no arquivo " + arquivo);
            }
            if (!valorPropriedade.equalsIgnoreCase(os)) {
                LOG.warn("O sistema operacional configurado no arquivo " + arquivo
                        + " é diferente do sistema operacional atual. valor atual" + valorPropriedade);
                LOG.info("Vou alterar o valor da propriedade 'sistema.operacional' para \"" + os + "\"");
                try {
                    alterarValorProps("sistema.operacional", os.toUpperCase());
                    LOG.info("Propriedade 'sistema.operacional' alterada com sucesso");
                } catch (Exception e) {
                    LOG.error("Houve um erro ao tentar alterar o valor da propriedade 'sistema.operacional'.", e);
                    throw e;
                }
            }

            verificaSeFoiAplicadoConfiguracaoBanco();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private static void verificaSeFoiAplicadoConfiguracaoBanco() throws Exception {
        Properties properties = new Properties();

        try (InputStream inputStream = Iniciador.class.getClassLoader().getResourceAsStream(arquivo)) {
            properties.load(inputStream);
            String valorPropriedade = obterPropriedade("config.banco.aplicado");
            if (valorPropriedade.equalsIgnoreCase("false")) {
                LOG.info("Vou aplicar as configurações do banco de dados");
                executeScript();
            } else {
                LOG.info(
                        "As configurações do banco de dados já foram aplicadas. Vou continuar a inicialização da aplicação.");
            }
        } catch (Exception e) {
            LOG.error("Houve um erro ao ler o " + arquivo, e);
            ;
            throw e;
        }
    }

    private static void executeScript() {
        try {
            String apli = obterPropriedade("config.banco.aplicado");
            if(apli.equalsIgnoreCase("false")){
                String pro = "";
                if (os.equalsIgnoreCase("WINDOWS")) {
                    pro = "cmd.exe /c start";
                } else {
                    pro = "bash";
                }

                String script = obterScript();

                LOG.info("Vou executar o seguinte script \n" + script);

                ProcessBuilder builder = new ProcessBuilder(pro, script);
                builder.redirectErrorStream(true);

                Process process = builder.start();

                LOG.info("Script executado com sucesso");

                alterarValorProps("config.banco.aplicado", "true");
            } else {
                LOG.info("As configurações do banco de dados já foram aplicadas. Vou continuar a inicialização da aplicação.");
            }

        } catch (Exception e) {
            LOG.error("Houve um erro ao tentar executar o script", e);
        }
    }

    private static void alterarValorProps(String chave, String valor) throws Exception {
        try {
            Properties properties = new Properties();
            properties.setProperty("sistema.operacional", os);
            OutputStream outputStream = new FileOutputStream(arquivo);
            properties.store(outputStream, null);
            outputStream.close();
            LOG.info("Propriedade campo \"" + chave + "\" alterada com sucesso para = " + valor);
        } catch (Exception e) {
            LOG.error("Houve um erro ao tentar alterar o valor da propriedade '"+chave+"'.", e);
            throw e;
        }
    }

    private static String obterPropriedade(String chave) throws Exception {
        Properties properties = new Properties();

        try (InputStream inputStream = Iniciador.class.getClassLoader().getResourceAsStream(arquivo)) {
            properties.load(inputStream);
            String valorPropriedade = properties.getProperty(chave);
            return valorPropriedade;
        } catch (Exception e) {
            LOG.error("Houve um erro a propriedade " + chave + " e obter o seu valor.", e);
            throw e;
        }
    }

    private static String obterScript() throws Exception {
        String caminhoScript = "";
        if (os.equalsIgnoreCase("windows")) {
            caminhoScript = "scripts/configuracao_banco_windows.bat";
        } else {
            caminhoScript = "scripts/configuracao_banco_linux.sh";
        }

        InputStream inputStream = Iniciador.class.getClassLoader().getResourceAsStream(caminhoScript);

        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String linha = "";
                StringBuilder scriptBuilder = new StringBuilder();
                while ((linha = reader.readLine()) != null) {
                    scriptBuilder.append(linha).append("\n");
                }

                return scriptBuilder.toString();
            } catch (Exception e) {
                LOG.error("Houve um erro ao tentar ler o script " + caminhoScript, e);
                throw e;
            }
        } else {
            LOG.error("O script " + caminhoScript + " não foi encontrado");
            throw new Exception("O script " + caminhoScript + " não foi encontrado");
        }
    }

}
