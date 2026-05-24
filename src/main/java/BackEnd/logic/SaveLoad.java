package BackEnd.logic;

import BackEnd.Bibliotheque;
import BackEnd.Emprunt;
import BackEnd.Livres.Livre;
import BackEnd.Usager.Usager;
import BackEnd.logic.BackgroundTask.ReparationTerminer;
import BackEnd.logic.BackgroundTask.RetourDeLivre;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveLoad {

    // custum GsonBuilder pour LocalDate (non sérialisable)
    private static Gson buildGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                        (date, type, ctx) -> new JsonPrimitive(date.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                        (json, type, ctx) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString()))
                .create();
    }

    public static synchronized void save(Bibliotheque bibliotheque, RetourDeLivre retour, ReparationTerminer reparation) throws IOException{
        Gson gson = buildGson();

        // objet pour encapsuler tout les object à sauvegarder
        JsonObject data = new JsonObject();
        data.add("livres", gson.toJsonTree(bibliotheque.getListeLivres()));
        data.add("emprunt", gson.toJsonTree(bibliotheque.getListeEmprunt()));
        data.add("usager", gson.toJsonTree(bibliotheque.getListeUsager()));
        data.add("livreBrise", gson.toJsonTree(bibliotheque.getListeBrise()));
        data.addProperty("totalEmprunt", bibliotheque.getTotalEmprunt());
        data.addProperty("userCount", Usager.getCount());
        data.add("dernierScanRetour",  gson.toJsonTree(retour.getDernierScan()));
        data.add("dernierScanReparation", gson.toJsonTree(reparation.getDernierScan()));

        // responsabilité du catch à l'appellant
        try (FileWriter writer = new FileWriter("bibliotheque.json", StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        }
    }

    public static synchronized boolean load(Bibliotheque bibliotheque, RetourDeLivre retour, ReparationTerminer reparation) throws IOException {
        File file = new File("bibliotheque.json");
        if (file.exists()) {

            Gson gson = buildGson();

            // responsabilité du catch à l'appellant
            try (FileReader reader = new FileReader("bibliotheque.json", StandardCharsets.UTF_8)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);

                // reconstruction des objets + set
                bibliotheque.setListeLivres(gson.fromJson(json.get("livres"), new TypeToken<ArrayList<Livre>>() {
                }.getType()));
                bibliotheque.setListeEmprunt(gson.fromJson(json.get("emprunt"), new TypeToken<ArrayList<Emprunt>>() {
                }.getType()));
                bibliotheque.setListeUsager(gson.fromJson(json.get("usager"), new TypeToken<ArrayList<Usager>>() {
                }.getType()));
                bibliotheque.setListeBrise(gson.fromJson(json.get("livreBrise"), new TypeToken<ArrayList<Emprunt>>() {
                }.getType()));
                bibliotheque.setTotalEmprunt(new AtomicInteger(json.get("totalEmprunt").getAsInt()));
                Usager.setCount(new AtomicInteger(json.get("userCount").getAsInt()));
                retour.setDernierScan(gson.fromJson(json.get("dernierScanRetour"), LocalDate.class));
                reparation.setDernierScan(gson.fromJson(json.get("dernierScanReparation"), LocalDate.class));
            }
            return true;
        }
        return false;
    }
}
