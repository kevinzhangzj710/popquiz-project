package cn.edu.njust.hearth.popquiz_backend;

import java.io.*;

public class AudioConverter {
    public static boolean convert(File source, File target) {
        String cmd="";
        String os=System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb = new ProcessBuilder();
        if(os.contains("win")){
            cmd = String.format("ffmpeg -y -i \"%s\" -ar 16000 \"%s\"", source.getAbsolutePath(), target.getAbsolutePath());
            pb.command("cmd.exe","/c",cmd);
        }else{
            cmd=String.format("ffmpeg -y -i %s -ar 16000 %s", source.getAbsolutePath(), target.getAbsolutePath());
            pb.command("bash","-c",cmd);
        }
        try {
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                System.out.println("转换成功: " + target.getAbsolutePath());
                return true;
            } else {
                System.err.println("转换失败，退出代码: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
