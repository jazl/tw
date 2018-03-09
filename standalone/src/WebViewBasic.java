import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// From here: https://gist.github.com/skrb/2333689

public class WebViewBasic extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        //String _url = "http://fakessofortesting.azurewebsites.net/SSO/Login/testing";
        //String _url = "https://fodqa4-tenant.fortifyfodqa4.local/SSO/Login/51b1882b-5d5c-4b62-b49d-2bf8b21772ee";
        String _url = "https://ams.fortify.com/SSO/Login/e3bdf1ea-4b3a-4956-967f-bfadac5298cd";
        //String _url = "http://localhost:4200/";
        //String _url = "http://localhost:3000/";
        StackPane root = new StackPane();

        WebView view = new WebView();
        WebEngine engine = view.getEngine();

        engine.setJavaScriptEnabled(true);
        CookieHandler.setDefault(new CookieManager());

        final Worker<Void> loadWorker = engine.getLoadWorker();

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {

//                System.out.println("stateProperty observable.getValue() = "+observable.getValue());
//                System.out.println("loadworker.getMessage = "+loadWorker.getMessage());
//                System.out.println("loadworker.getProgress = "+loadWorker.getProgress());
//                System.out.println("loadworker.getWorkDone = "+loadWorker.getWorkDone());
//                System.out.println("loadworker.getTotalWork = "+loadWorker.getTotalWork());

            }
        });

        engine.locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("locationProperty observable.getValue() = "+observable.getValue());
            }
        });

//        engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
//            @Override
//            public void handle(WebEvent<String> event) {
//                System.out.println("setOnStatusChanged = "+event);
//            }
//        });
        System.out.println("_url = "+_url);
        engine.load(_url);

        root.getChildren().add(view);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        Application.launch(args);
    }
}
