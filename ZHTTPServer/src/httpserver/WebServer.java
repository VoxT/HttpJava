package httpserver;

import javax.servlet.Filter;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jetty.servlet.ServletHolder;
import http.api.path.ZPathAPI;
import http.api.servlet.ZPApiServlet;
import http.config.ZConfigReader;

public class WebServer extends Thread {
    
    private static final Logger logger = Logger.getLogger(WebServer.class);
    
    @Override
    public void run() {
        try {
            this.startWebServer();
        } catch (Exception ex) {
            logger.error("Run server error " + ex.getMessage());
        }
    }
    
    public void startWebServer() throws Exception {
        Server server = new Server();
        
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(ZConfigReader.getConfigReaderIns().getJettyMinThreads());
        threadPool.setMaxThreads(ZConfigReader.getConfigReaderIns().getJettyMaxThreads());
        server.setThreadPool(threadPool);
        
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(ZConfigReader.getConfigReaderIns().getJettyPortListen());//Set port
        connector.setLowResourcesConnections(ZConfigReader.getConfigReaderIns().getJettyLowResourcesConnections());
        connector.setAcceptors(ZConfigReader.getConfigReaderIns().getJettyAcceptors());
        
        server.setConnectors(new Connector[]{
            connector
        });
        
        ServletHandler handler = new ServletHandler();

        //http://localhost:8401/notify/apisample
        //viewer servlet
        //set path API
        ServletHolder holderPub = handler.addServletWithMapping(ZPApiServlet.class, "/" + ZPathAPI.API_ROOT_PATH + "/*");
        holderPub.setAsyncSupported(true);
        FilterHolder gzipFilterHolder = this.createGzipFilterHolder();
        handler.addFilter(gzipFilterHolder, this.createFilterMapping("/*", gzipFilterHolder));
        
        server.setHandler(handler);
        server.setStopAtShutdown(true);
        server.setGracefulShutdown(1000);//1 giay se dong
        server.setSendServerVersion(false);
        
        ShutdownThread obj = new ShutdownThread(server);
        Runtime.getRuntime().addShutdownHook(obj);
        server.start();
        server.join();
        
    }
    
    private FilterHolder createGzipFilterHolder() {
        Filter gzip = new GzipFilter();
        FilterHolder filterHolder = new FilterHolder(gzip);
        filterHolder.setName("gzip");
        return filterHolder;
    }
    
    private FilterMapping createFilterMapping(String pathSpec, FilterHolder filterHolder) {
        FilterMapping filterMapping = new FilterMapping();
        filterMapping.setPathSpec(pathSpec);
        filterMapping.setFilterName(filterHolder.getName());
        return filterMapping;
    }
}
