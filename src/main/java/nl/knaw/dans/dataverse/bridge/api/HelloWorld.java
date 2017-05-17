package nl.knaw.dans.dataverse.bridge.api;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by akmi on 10/05/17.
 */
public class HelloWorld {
    public static void main(String[] args) {

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("res/nashorn1.js"));
            lines.add("print('foobar');");
            Files.write(Paths.get("res/nashorn1-modified.js"), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        Flowable.just("Hello world").subscribe(System.out::println);
        Flowable.fromCallable(() -> {
            System.out.println("0");
            Thread.sleep(10000); //  imitate expensive computation
            return "Done";
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(System.out::println, Throwable::printStackTrace);

        try {
            System.out.println("1");
            Thread.sleep(30000);
            System.out.println("2");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}