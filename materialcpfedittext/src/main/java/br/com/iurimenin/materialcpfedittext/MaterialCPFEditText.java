package br.com.iurimenin.materialcpfedittext;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by iurimenin on 11/09/16.
 */
public class MaterialCPFEditText extends MaterialEditText {

    private boolean isUpdating;

    /*
     * Mapeia o cursor para a posição que você quer o inteiro na tela + 1
     * (+1 para o Padding funcionar. Se chegar no último caracter, e não ter um espaço vazio no próximo, irá travar).
     * => 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
     *    # # # . # # # . # # #  -  #  #
     */
    private int positioning[] = { 0, 1, 2, 3, 5, 6, 7, 9, 10, 11, 13, 14};


    public MaterialCPFEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public MaterialCPFEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MaterialCPFEditText(Context context) {
        super(context);
        initialize();
    }

    public String getCleanText() {
        String text = MaterialCPFEditText.this.getText().toString();

        text.replaceAll("[^0-9]*", "");
        return text;
    }

    private void initialize() {
        //Número de caracteres na tela, contando os "." e "-" e sempre +1.
        final int maxNumberLength = 14;
        this.setKeyListener(keylistenerNumber);

        this.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String current = s.toString();

				/*
				 * Quando termina de preencher o número, seta como falso
				 * para não precisar ficar reprocessando o campo
				 */
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

				/* Retira qualquer caracter que não nos importa */
                String number = current.replaceAll("[^0-9]*", "");
                if (number.length() > 11)
                    number = number.substring(0, 11);

                int length = number.length();

				/* da um Pad no numero para 9 caracteres */
                String paddedNumber = padNumber(number, maxNumberLength);

				/* Corta a string do Cpf nas partes que nos interessam */
                String part1 = paddedNumber.substring(0, 3);
                String part2 = paddedNumber.substring(3, 6);
                String part3 = paddedNumber.substring(6, 9);
                String part4 = paddedNumber.substring(9, 11);

				/* Cria a String para colocar na tela */
                String cpf = part1 + "." + part2 + "." + part3 + "-" + part4 ;

				/*
				 * Seta essa flag, então na proxima execução
				 * do afterTextChanged não ira fazer nada
				 */
                isUpdating = true;
                if (length > 0) {
                    MaterialCPFEditText.this.setText(cpf);
                    MaterialCPFEditText.this.setSelection(positioning[length]);
                } else {
                    MaterialCPFEditText.this.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    protected String padNumber(String number, int maxLength) {
        String padded = new String(number);
        for (int i = 0; i < maxLength - number.length(); i++)
            padded += " ";
        return padded;
    }

    private final KeylistenerNumber keylistenerNumber = new KeylistenerNumber();

    private class KeylistenerNumber extends NumberKeyListener {

        public int getInputType() {
            return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        }

        @Override
        protected char[] getAcceptedChars() {
            return new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        }
    }
}