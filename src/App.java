import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    private static Map<Integer, List<String>> dic;
    public static void main(String[] args) throws Exception {
        dic = loadDictionnary();
        List<String> L = null;

        List<String> allErrorList = new ArrayList<>();

        int nombreDeCantiques = 0;
        int nombreDerreur = 0;
        for (int i = 1; i < 655; i++) {
            L = getMotsMalEcrisDuCantique(i);
            if(!L.isEmpty()){
                nombreDeCantiques++;
                nombreDerreur += L.size();
                allErrorList.addAll(L);
                System.out.println("-------> " + i + "<-------");
                System.out.println(L);
            }
        }
        for (int i = 1; i < 655; i++) {
            L = getMotsMalEcrisDuCantique(i);
            if(!L.isEmpty()){
                System.out.println("-------> " + i + "<-------");
                System.out.println(L);
                afficheErreurCantique(i, L);
                break;
            }
        }

        Map<String, Error> errors = new HashMap<>();
        for (String key : allErrorList) {
            if(errors.get(key) == null) {
                errors.put(key, new Error(key));
            } else {
                errors.get(key).times++;
            }
        }
        Optional<App.Error> max = errors.values().stream().max(Comparator.comparing(Error::getTimes));
        
        System.out.println("\n\nNombre de cantiques ayant des erreurs : " + nombreDeCantiques);
        System.out.println("Nombre d'erreurs : " + nombreDerreur);
        if(max.isPresent()) System.out.println("Plus frequent : " + max.get().word + " : " + max.get().times);
    }

    public static Map<Integer, List<String>> loadDictionnary() {
        Map<Integer, List<String>> dic = new HashMap<>();
        File f = new File("src/dic/fr.dic");
        try (Scanner sc = new Scanner(f, Charset.forName("utf-8"));) {
        String line = null;
        while(sc.hasNextLine()){
            line = sc.nextLine();
            if(dic.get(line.length()) == null)
                dic.put(line.length(), new ArrayList<>());
            
            dic.get(line.length()).add(line.toLowerCase());
        }
        } catch (Exception e) {
        }
        return dic;
    }

    public static List<String> getMotsMalEcrisDuCantique(int i){
        List<String> L = new ArrayList<>();
        if(i < 1 || i > 654) return L;

        File f = new File("src/textes/H"+i+".txt");
        
        try (Stream<String> stream = Files.lines(f.toPath(), Charset.forName("utf-8"))) {
        String collect = stream.collect(Collectors.joining(" "))
                            .replaceAll(",", " ")
                            .replaceAll("\\.", " ")
                            .replaceAll("'", " ")
                            .replaceAll("-", " ")
                            .replaceAll("\\!", " ")
                            .replaceAll("\\?", " ")
                            .replaceAll(";", " ")
                            .replaceAll(":", " ")
                            .replaceAll("\"", " ")
                            .replaceAll("\\(", " ")
                            .replaceAll("\\)", " ")
                            .replaceAll("'", " ")
                            .replaceAll("\u00BB", " ")
                            .replaceAll("\u00AB", " ")
                            .replaceAll("\\{", " ")
                            .replaceAll(":", " ")
                            .replaceAll("    ", " ")
                            .replaceAll("   ", " ")
                            .replaceAll("  ", " ");
        L = Stream.of(collect.split(" ")).filter(s -> s.trim().length() > 0 && isNotInDic(s)).distinct().toList();    
        } catch (Exception e) {
        }
        return L;
    }

    public static boolean isNotInDic(String word){
        try {
            Integer.parseInt(word.trim());
            return false;    
        } catch (Exception e) {
        }
        return !dic.get(word.trim().toLowerCase().length()).contains(word.trim().toLowerCase());
    }

    private static void afficheErreurCantique(int i, List<String> errors) {
        File f = new File("src/textes/H"+i+".txt");
        
        try (Stream<String> stream = Files.lines(f.toPath(), Charset.forName("utf-8"))) {
        String collect = stream.collect(Collectors.joining("\n"));    
        for (String error : errors) {
            collect = collect.toLowerCase().replaceAll(error, "XXXXXX");    
        }
        System.out.println(collect);
    } catch (Exception e) {
        }
        
    }

    private static class Error{
        private String word; 
        public String getWord() {
            return word;
        }
        private int times = 0;
        public int getTimes() {
            return times;
        }
        Error(String word){
            this.word = word;
            this.times = 1;
        }
    }
}
