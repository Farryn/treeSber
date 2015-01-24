package tree;



import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class HierarchicalTree {
    static int n;                                   //число строк в таблице
    static int lastIndex=0;
    static int lvl=0;                               //отступ для вывода
    static String[][] arr;                          //исходная таблица
    static String[][] res;                          //результат

    public static void main(String[] args) {

        getTable();
        getTree(0);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < Integer.valueOf(res[i][1]); j++) {
                System.out.print(" ");
            }
            System.out.println(res[i][0]);
        }
    }

    private static void getTable() {                        //соединение с базой и считывание таблицы
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            BufferedReader reader = new BufferedReader(new FileReader("config.txt")); //считываем данные из конфига
            String line = null;
            String[] connectInfo=new String[3];
            int j=0;
            while ((line = reader.readLine()) != null) {
                String[] tmp=line.split(" - ");
                connectInfo[j]=tmp[1];
                j++;
            }


            con = DriverManager.getConnection("jdbc:mysql://"+connectInfo[0],connectInfo[1],connectInfo[2]); //устанавливаем соединение


            pst = con.prepareStatement("SELECT COUNT(*) FROM spravp ORDER BY pid");     //определяем число строк в таблице
            rs=pst.executeQuery();
            rs.next();
            n=rs.getInt(1);
            arr=new String[n][3];
            res=new String[n][2];

            pst = con.prepareStatement("SELECT * FROM spravp ORDER BY pid");            //получаем таблицу
            rs=pst.executeQuery();
            int i=0;
            while (rs.next()) {
                arr[i][0]=String.valueOf(rs.getInt(1));
                arr[i][1]=rs.getString(2);
                arr[i][2]=String.valueOf(rs.getInt(3));
                i++;
            }
        } catch (Exception ex) {
            System.out.println("Something is wrong");
            System.out.println(ex.getMessage());
        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                System.out.println("Something is wrong");
            }
        }

    }

    private static void getTree(int par) {                          //построение дерева
        int flag=0;
        for (int i = 0; i < n; i++) {
            if(Integer.valueOf(arr[i][2])==par){
                res[lastIndex][0]=arr[i][1];
                res[lastIndex][1]=String.valueOf(lvl);
                lastIndex++;
                lvl++;
                getTree(Integer.valueOf(arr[i][0]));
                flag=1;
            }else{
                if(flag==1)break;
            }
        }
        lvl--;
    }
}
