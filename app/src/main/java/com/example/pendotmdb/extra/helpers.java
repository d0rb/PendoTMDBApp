package com.example.pendotmdb.extra;

import android.util.Log;

import com.example.pendotmdb.objects.movieObj;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class helpers {
    // geek4geek function to compare between two strings.
    // 0 match , 1 no match.
    /**
     *
     * @param str1 string 1 to compare
     * @param str2 string 2 to compare
     * @return 0 there is a math , 1 no match.
     */
    public static int stringCompare(String str1, String str2)
    {
        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }

        if (l1 != l2) {
            return l1 - l2;
        }

        else {
            return 0;
        }
    }
    /**
     *
     * @param list list based on movieObj , should be the main movie list.
     * @param string string to compare with , in this case this is the string representing the position from autocomplete selection.
     * @return
     *
     Since autocomplete does not return position based on movieList's array rather on it's own array ,
     this function idea is to find the correct movie's position on the main movie list based on string matching ( stringCompare ).
     */
    public static int getMovieId(List<movieObj> list, String string){
        for (int i = 0 ; i <list.size() ; i++){
            if ((helpers.stringCompare(list.get(i).getTitle(), string))==0){
                return i;
            }
        }
        return -1;
    }

    /**
     *
     * @return current date  ex yyyy-MM-dd / 2021-01-01 , as string
     */
    public static String getCurrentDate() {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(calendar.getTime());
    }
}
