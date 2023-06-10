package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView resultTv, solutionTv;
    MaterialButton buttonC, buttonBrackOpen, buttonBrackClose;
    MaterialButton buttonDivide, buttonMultiply, buttonMinus, buttonEquals, buttonPlus;
    MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    MaterialButton buttonAC, buttonDot;

    static ArrayList<String> historyList;
    ArrayAdapter<String> adapter;
    private MyDatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        historyList = new ArrayList<>();
        databaseHelper = new MyDatabaseHelper(this);


        assignId(buttonC, R.id.button_c);
        assignId(buttonBrackOpen, R.id.button_open_bracket);
        assignId(buttonBrackClose, R.id.button_close_bracket);
        assignId(buttonDivide, R.id.button_divide);
        assignId(buttonMultiply, R.id.button_multiply);
        assignId(buttonPlus, R.id.button_plus);
        assignId(buttonMinus, R.id.button_minus);
        assignId(buttonEquals, R.id.button_equals);
        assignId(button0, R.id.button_0);
        assignId(button1, R.id.button_1);
        assignId(button2, R.id.button_2);
        assignId(button3, R.id.button_3);
        assignId(button4, R.id.button_4);
        assignId(button5, R.id.button_5);
        assignId(button6, R.id.button_6);
        assignId(button7, R.id.button_7);
        assignId(button8, R.id.button_8);
        assignId(button9, R.id.button_9);
        assignId(buttonAC, R.id.button_ac);
        assignId(buttonDot, R.id.button_dot);
        Button buttonHistory = findViewById(R.id.button_history);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                // intent.putStringArrayListExtra("historyList", historyList);
                startActivity(intent);
            }
        });
    }



    void assignId(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate = solutionTv.getText().toString();

        if (buttonText.equals("AC")) {
            solutionTv.setText("");
            resultTv.setText("");
            return;
        }
        if (buttonText.equals("=")) {
            String operation = dataToCalculate;
            solutionTv.setText(resultTv.getText());
            //String operation = dataToCalculate;
            String result = resultTv.getText().toString();
            String finalResult = getResult(dataToCalculate);
            if (finalResult.startsWith("Inf") || finalResult.startsWith("syntax")) {
                resultTv.setText("syntax error");
                // Toast.makeText(getApplicationContext(), "Impossible de diviser par 0", Toast.LENGTH_LONG).show();

            } else if (finalResult.startsWith("syntax")) {
                resultTv.setText("syntax error");
                Toast.makeText(getApplicationContext(), "syntax error", Toast.LENGTH_LONG).show();

            }else if (finalResult.startsWith("Err")) {
                resultTv.setText("syntax error");
                Toast.makeText(getApplicationContext(), "syntax error", Toast.LENGTH_LONG).show();

            } else {
                // historyList.add(operation + " = " + result);
                String equation = operation + " = " + result;
                databaseHelper.insertEquation(equation);
            }

           /* if (historyList.size() > 10) {
                historyList.remove(0);
            }*/
            return;
        }
        if (buttonText.equals("C")) {
            if(dataToCalculate.length()>1){
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            }
            else if(dataToCalculate.length() <= 1){
                solutionTv.setText("");
                resultTv.setText("");
                return;

            }
        } else {
            dataToCalculate = dataToCalculate + buttonText;
        }
        solutionTv.setText(dataToCalculate);

        String finalResult = getResult(dataToCalculate);

        if (!finalResult.equals("Err")) {
            resultTv.setText(finalResult);
           // String operation = dataToCalculate;
           // String result = finalResult;

            // Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            // intent.putExtra("operation", operation);
            //intent.putExtra("result", result);
            //startActivity(intent);
        }



    }

    String getResult(String data) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initSafeStandardObjects();
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "");
            } else if (finalResult.startsWith("Inf")) {
                //resultTv.setText("Non divisible par 0");
                Toast.makeText(getApplicationContext(), "Impossible de diviser par 0", Toast.LENGTH_LONG).show();
                return "syntax error";
            }else if (finalResult.startsWith("NaN")) {
                //resultTv.setText("Non divisible par 0");
                Toast.makeText(getApplicationContext(), "Impossible de diviser par 0", Toast.LENGTH_LONG).show();
                return "syntax error";
            }
            return finalResult;
        } catch (Exception e) {
            return "Err";
        }
    }


}
