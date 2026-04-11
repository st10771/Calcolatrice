package com.example.calcolatrice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private String currentInput = "";
    private String operator = "";
    private double firstValue = Double.NaN;
    private boolean isNewOp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // Listener per i numeri
        View.OnClickListener numberListener = v -> {
            Button b = (Button) v;
            if (isNewOp) {
                currentInput = b.getText().toString();
                isNewOp = false;
            } else {
                if (currentInput.equals("0")) {
                    currentInput = b.getText().toString();
                } else {
                    currentInput += b.getText().toString();
                }
            }
            updateDisplay();
        };

        // Assegnazione listener ai pulsanti numerici (comuni a entrambi i layout)
        int[] numberIds = {R.id.t0, R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6, R.id.t7, R.id.t8, R.id.t9};
        for (int id : numberIds) {
            View btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(numberListener);
        }

        // Punto decimale
        View punto = findViewById(R.id.punto);
        if (punto != null) {
            punto.setOnClickListener(v -> {
                if (isNewOp) {
                    currentInput = "0.";
                    isNewOp = false;
                } else if (!currentInput.contains(".")) {
                    currentInput += currentInput.isEmpty() ? "0." : ".";
                }
                updateDisplay();
            });
        }

        // Operazioni base
        View.OnClickListener operatorListener = v -> {
            Button b = (Button) v;
            if (!currentInput.isEmpty() || !Double.isNaN(firstValue)) {
                if (!currentInput.isEmpty()) {
                    calculate();
                }
                operator = b.getText().toString();
                firstValue = Double.parseDouble(display.getText().toString());
                isNewOp = true;
            }
        };

        int[] opIds = {R.id.piu, R.id.meno, R.id.moltiplicazione, R.id.divisione};
        for (int id : opIds) {
            View btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(operatorListener);
        }

        // Uguale
        View uguale = findViewById(R.id.uguale);
        if (uguale != null) {
            uguale.setOnClickListener(v -> {
                calculate();
                operator = "";
                isNewOp = true;
            });
        }

        // Cancella (C)
        View cancella = findViewById(R.id.cancella);
        if (cancella != null) {
            cancella.setOnClickListener(v -> {
                currentInput = "";
                firstValue = Double.NaN;
                operator = "";
                isNewOp = true;
                display.setText("0");
            });
        }

        // Funzioni Scientifiche (solo layout orizzontale)
        setupScientificFunctions();
    }

    private void setupScientificFunctions() {
        // Radice quadrata
        View radice = findViewById(R.id.radice);
        if (radice != null) {
            radice.setOnClickListener(v -> {
                double val = Double.parseDouble(display.getText().toString());
                if (val >= 0) {
                    displayResult(Math.sqrt(val));
                } else {
                    display.setText("Errore");
                }
                isNewOp = true;
            });
        }

        // Logaritmo base 10
        View log10 = findViewById(R.id.log10);
        if (log10 != null) {
            log10.setOnClickListener(v -> {
                double val = Double.parseDouble(display.getText().toString());
                if (val > 0) {
                    displayResult(Math.log10(val));
                } else {
                    display.setText("Errore");
                }
                isNewOp = true;
            });
        }

        // Potenza (x^y) - trattata come operatore binario
        View potenza = findViewById(R.id.potenza);
        if (potenza != null) {
            potenza.setOnClickListener(v -> {
                if (!currentInput.isEmpty()) calculate();
                operator = "x^y";
                firstValue = Double.parseDouble(display.getText().toString());
                isNewOp = true;
            });
        }

        // Reciproco (1/x)
        View reciproco = findViewById(R.id.reciproco);
        if (reciproco != null) {
            reciproco.setOnClickListener(v -> {
                double val = Double.parseDouble(display.getText().toString());
                if (val != 0) {
                    displayResult(1.0 / val);
                } else {
                    display.setText("Errore");
                }
                isNewOp = true;
            });
        }

        // Fattoriale (x!)
        View fattoriale = findViewById(R.id.fattoriale);
        if (fattoriale != null) {
            fattoriale.setOnClickListener(v -> {
                double val = Double.parseDouble(display.getText().toString());
                if (val >= 0 && val == (long) val && val <= 20) {
                    long res = 1;
                    for (int i = 1; i <= (long) val; i++) res *= i;
                    displayResult((double) res);
                } else {
                    display.setText("Errore");
                }
                isNewOp = true;
            });
        }
    }

    private void updateDisplay() {
        display.setText(currentInput.isEmpty() ? "0" : currentInput);
    }

    private void calculate() {
        if (!Double.isNaN(firstValue) && !currentInput.isEmpty()) {
            double secondValue = Double.parseDouble(currentInput);
            double result = 0;
            boolean error = false;

            switch (operator) {
                case "+": result = firstValue + secondValue; break;
                case "-": result = firstValue - secondValue; break;
                case "×": result = firstValue * secondValue; break;
                case "÷":
                    if (secondValue != 0) result = firstValue / secondValue;
                    else error = true;
                    break;
                case "x^y": result = Math.pow(firstValue, secondValue); break;
                default: return;
            }

            if (error) {
                display.setText("Errore");
                firstValue = Double.NaN;
            } else {
                displayResult(result);
                firstValue = result;
            }
            currentInput = "";
        }
    }

    private void displayResult(double value) {
        if (value == (long) value) {
            display.setText(String.format(Locale.getDefault(), "%d", (long) value));
        } else {
            display.setText(String.valueOf(value));
        }
    }
}
