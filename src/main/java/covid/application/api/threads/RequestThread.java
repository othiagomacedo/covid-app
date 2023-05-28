package covid.application.api.threads;

import covid.application.api.external.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestThread extends Thread{

    private static Logger LOG = LoggerFactory.getLogger(RequestThread.class);

    String url;

    public RequestThread(String urlRequest) {
        long id = this.getId();
        LOG.info("ID Thread para esta instância de Thread é " + id);
        this.url = urlRequest;
    }


    @Override
    public void run() {
        try {
            String result = Requests.realizarRequest(url);
            RequestThreadRepository.addLista(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
