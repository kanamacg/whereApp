package whereapp.kps.cpe.com.wheresapp;

/**
 * Created by apple on 4/22/15 AD.
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {

    public static void writing(String str , String user) {

        String user1=user+".txt";
        try {



            String path = "/storage/emulated/0/"+user1;

            if(path!=null) {
                File file = new File(path);

                /*** if exists create text file ***/
                if (!file.exists()) {
                    file.createNewFile();
                }

                /*** Write Text File ***/
                FileWriter writer = new FileWriter(file, true); //True = Append to file, false = Overwrite
                writer.write(str + "\n");
                writer.close();
            }



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }
}
