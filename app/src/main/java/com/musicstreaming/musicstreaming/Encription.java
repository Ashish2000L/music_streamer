package com.musicstreaming.musicstreaming;

import java.util.Random;

public class Encription {

    private Random random;


    public Encription() {
        random=new Random();
    }

    public String Encript(String text){

        char[] ch=correct_text_length(text).toCharArray();

        char[][] random_val = new char[16][8];

        for(int j=0; j<ch.length; j++){

            for(int i=0; i<8; i+=2){

                int ran = random.nextInt(9)+35;
                int val;
                if((int)ch[j]+ran<=122 ){
                    ch[j]=(char)((int)ch[j]+ran);
                    val = random.nextInt(26)+65;
                }else{
                    ch[j]=(char)((int)ch[j]-ran);
                    val = random.nextInt(26)+97;
                }

                random_val[j][i]=(char)ran;
                random_val[j][i+1]=(char)val;
            }
        }

        return to_hex(combineAll(ch,random_val))+"/1";
    }

    public String combineAll(char[] text, char[][] rand){
        String combine_str="";

        for(int i=0;i<16;i++){
            combine_str+=text[i];
            for (int j=0;j<8;j++){

                combine_str+=rand[i][j];

            }

        }

        return combine_str;
    }

    public String correct_text_length(String text){

        if(text.length()<16){
            text+=".";
            while((16-text.length())>0){
                int value =random.nextInt(26)+65;
                text+=(char)value;
            }
        }
        return text;
    }

    public String to_hex(String encoding){

        char[] ch=encoding.toCharArray();
        String final_encoding="";
        for(char c : ch){

            final_encoding+=Integer.toHexString((int)c);

        }
        return final_encoding;
    }

    public static class Decription{

        public static String[] parts = new String[16];
        public static char[] txt = new char[16];
        public static char[][] rand = new char[16][8];

        public Decription() {
        }

        public String Decript(String enc_txt){

            makeParts(enc_txt);

            seperate_txt();

            return decriptTxt();
        }

        public static String decriptTxt(){

            String text="";
            int i=0;
            for(char val : txt){

                for(int j=0;j<8;j+=2){

                    if(Character.isUpperCase(rand[i][j+1])){

                        txt[i]=(char)((int)txt[i]-rand[i][j]);

                    }else{

                        txt[i]=(char)((int)txt[i]+rand[i][j]);

                    }

                }
                text+=txt[i];
                i++;
            }


            return text.split("\\.")[0];
        }

        public static void seperate_txt(){
            int i=0;
            for(String str : parts){

                txt[i]=(char)Integer.parseInt(str.substring(0,2), 16 );
                int j=0;
                for(int p=2;p<18;p+=2){

                    rand[i][j]=(char)Integer.parseInt(str.substring(p,p+2), 16 );
                    j++;
                }
                i++;
            }

        }

        public static void makeParts(String txt){
            int p=0;
            for(int i=0;i<txt.length(); i+=18){

                parts[p]=txt.substring(i,i+18);
                p++;
            }
        }

    }
}
