package org.firstinspires.ftc.teamcode.code2026.sundae;

import com.bylazar.telemetry.TelemetryManager;
import com.google.gson.Gson;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class SaveManager {

    public void Save(OrderCollection collection){
        Gson gson = new Gson();
        String json = gson.toJson(collection);
        File file = new File(AppUtil.FIRST_FOLDER, "IceCreamOrderBackup.json");

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(json.getBytes());
        } catch (Exception exception) {


        }
    }
    public void Clear(){
        File file = new File(AppUtil.FIRST_FOLDER, "IceCreamOrderBackup.json");

        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
            } else {
            }
        }
    }
    public OrderCollection Read(){
        File file = new File(AppUtil.FIRST_FOLDER, "IceCreamOrderBackup.json");
        if(!file.exists()) return new OrderCollection();
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            OrderCollection collection = gson.fromJson(reader, OrderCollection.class);
            collection.successful = true;
            return collection;
        } catch (Exception e) {
            return new OrderCollection();
        }
    }
}
