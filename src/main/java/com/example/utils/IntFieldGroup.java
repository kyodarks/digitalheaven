package com.example.utils;

import javafx.scene.control.TextField;

public class IntFieldGroup {
    private CharField[] fields;

    public IntFieldGroup(TextField[] fields){
        this.fields = new CharField[fields.length];

        for (int i = 0; i < fields.length; i++) {
            this.fields[i] = new CharField(fields[i]);
        }

        makeConnections();
    }

    private void makeConnections(){
        for (int i = 0; i < fields.length; i++){
            final int index = i;
            CharField field = fields[index];
            field.getField().textProperty().addListener((o, oo, b)->{
                if (b.equals(field.getValue()+"")){return;}
                if (b.isBlank()){field.setValue("");}

                int len = b.length();
                String s = b.substring(0, Math.min(fields.length-index, len));

                for (int j = 0; j < s.length(); j++){
                    char a = s.charAt(j);
                    CharField currentField = fields[j + index];
                    boolean succes = currentField.setValue(String.valueOf(a));

                    if (succes){
                        currentField.getField().requestFocus();
                        currentField.getField().positionCaret(1);
                    }else{
                        String value = currentField.getValue()!=0? currentField.getValue()+"":"";

                        currentField.getField().setText(value);
                        break;
                    }
                }
            });
        }
    }

    public String getText(){
        return "";
    }

    private static class CharField{
        private TextField field;
        private int value;

        public CharField(TextField field){
            this.field = field;
            value = 0;
        }

        public TextField getField(){return field;}
        public int getValue(){return value;}
        public boolean setValue(String value){
            if (value == ""){this.value = 0;}
            if (value.length() > 1){return false;}
            try{this.value = Integer.parseInt(value);}catch(NumberFormatException e){return false;}
            field.setText(value);
            return true;
        }
    }

}
