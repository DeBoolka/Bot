package dikanev.nikita.bot.server;

import dikanev.nikita.bot.model.callback.CallbackApiHandler;
import dikanev.nikita.bot.model.storage.ServerStorage;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class CallbackRequestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CallbackRequestHandler.class);

    private final static String OK_BODY = "ok";
    private  static String respMessage = null;

    private final CallbackApiHandler callbackApiHandler;

    public CallbackRequestHandler() {
        callbackApiHandler = new CallbackApiHandler();
    }

    public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            return;
        }

        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        LOG.info("New req: " + body);
        boolean handled = callbackApiHandler.parse(body);
        if (!handled) {
            return;
        }

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        if (respMessage == null) {
            resp.getWriter().println(OK_BODY);
        } else {
            resp.getWriter().println(respMessage);
            respMessage = null;
        }
    }

    private void printTextResponse(HttpServletResponse resp, String text) {
        try (PrintWriter printWriter = resp.getWriter()) {
            printWriter.write(text);
        } catch (IOException ex) {
            LOG.warn("The client could not be contacted. Exception: " + ex.getMessage());
        }
    }

    public static void setRespMessage(String respMessage) {
        CallbackRequestHandler.respMessage = respMessage;
    }
}
