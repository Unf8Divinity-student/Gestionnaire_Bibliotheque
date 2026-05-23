package BackEnd.logic;

import BackEnd.Bibliotheque;
import BackEnd.Emprunt;
import BackEnd.Livres.Livre;
import BackEnd.Usager.Usager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveLoad {
    static synchronized void save(ArrayList<Livre> listeLivres, ArrayList<Emprunt> listeEmprunt, ArrayList<Usager> listeUsager, ArrayList<Livre> livreBrise, AtomicInteger totalEmprunt) throws IOException{
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // objet pour encapsuler tout les object à sauvegarder
        JsonObject data = new JsonObject();
        data.add("livres", gson.toJsonTree(listeLivres));
        data.add("emprunt", gson.toJsonTree(listeEmprunt));
        data.add("usager", gson.toJsonTree(listeUsager));
        data.add("livreBrise", gson.toJsonTree(livreBrise));
        data.addProperty("totalEmprunt", totalEmprunt.get());

        // responsabilité du catch à l'appellant
        try (FileWriter writer = new FileWriter("bibliotheque.json", StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        }
    }

    static synchronized boolean load(Bibliotheque bibliotheque) throws IOException {
        File file = new File("bibliotheque.json");
        if (file.exists()) {

            Gson gson = new Gson();

            try (FileReader reader = new FileReader("bibliotheque.json", StandardCharsets.UTF_8)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);

                // reconstruction des objets + set
                bibliotheque.setListeLivres(gson.fromJson(json.get("livres"), new TypeToken<ArrayList<Livre>>() {
                }.getType()));
                bibliotheque.setListeEmprunt(gson.fromJson(json.get("emprunt"), new TypeToken<ArrayList<Emprunt>>() {
                }.getType()));
                bibliotheque.setListeUsager(gson.fromJson(json.get("usager"), new TypeToken<ArrayList<Usager>>() {
                }.getType()));
                bibliotheque.setListeBrise(gson.fromJson(json.get("livreBrise"), new TypeToken<ArrayList<Livre>>() {
                }.getType()));
                bibliotheque.setTotalEmprunt(new AtomicInteger((json.get("totalEmprunt").getAsInt())));
            }
            return true;
        }
        return false;
    }
}
