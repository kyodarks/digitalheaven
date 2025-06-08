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

            field.getField().setOnKeyPressed(e->{
                String s = field.getField().getText();
                if (s.isBlank() 
                    && field.getValue() == 0
                    && field.getField().getCaretPosition() == 0
                    && index != 0
                    && e.getCode().toString().equals("BACK_SPACE")){
                        CharField x = fields[index-1];
                        x.getField().requestFocus();
                        x.getField().positionCaret(1);
                    }
            });

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

    public void setErrorStyle(){
        for (CharField x : fields) {
            x.getField().getStyleClass().add("error-field");
        }
    }

    public void removeErrorStyle(){
        for (CharField x : fields) {
            x.getField().getStyleClass().remove("error-field");
        }
    }

    public void resetInput(){
        for (CharField field : fields){
            field.getField().setText("");
        }
    }

    public int getValue(){
        char[] value = new char[fields.length];

        for (int i = 0; i < value.length; i++) {
            CharField field = fields[i];

            value[i] = Integer.toString(field.getValue()).charAt(0);
        }
        
        return Integer.parseInt(String.valueOf(value));
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
