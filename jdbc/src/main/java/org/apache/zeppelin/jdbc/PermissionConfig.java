package org.apache.zeppelin.jdbc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Properties;
import java.util.Arrays;
import java.util.stream.Stream;

public class PermissionConfig {

    public Set<String> controlUsers = new HashSet<>();
    public List<String> controlWords = new ArrayList<>();
    private String uStr = "";
    private String kStr = "";

    private String filepath = null;

    public PermissionConfig(String filepath) {
        this.filepath = filepath;
        updateConfig();
    }

    public void updateConfig() {
        if (filepath.isEmpty()) {
            return;
        }
        try {
            Properties pro = new Properties();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filepath)
            ));
            pro.load(br);
            br.close();

            Set<String> u = new HashSet<>();
            List<String> t = new ArrayList<>();
            uStr = pro.getProperty("user").toLowerCase();
            kStr = pro.getProperty("keyword").toLowerCase();
            u.addAll(Arrays.asList(uStr.split(" ")));
            t.addAll(Arrays.asList(kStr.split(" ")));
            controlUsers = u;
            controlWords = t;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBlock(String u, String sql) {
        if (controlUsers.contains(u)) {
            Stream<String> ss = Arrays.stream(sql.toLowerCase().split(" |\\n|\\r")).distinct();
            return ss.anyMatch(s -> controlWords.stream().anyMatch(
                    word -> s.contains(word)));
        }
        return false;
    }

    @Override
    public String toString() {
        return "PermissionConfig:\nuser=" + uStr + "\nkeyword=" + kStr;
    }
}
