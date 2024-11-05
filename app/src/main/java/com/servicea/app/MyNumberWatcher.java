package com.servicea.app;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.text.DecimalFormat;

import ir.servicea.R;

public class MyNumberWatcher implements TextWatcher {
  private EditText editText;
  private EditText editText1;
  private int digit;

  private boolean isChanged = false;


  public MyNumberWatcher(EditText editText, EditText editText1) {
    this.editText = editText;
    this.editText1 = editText1;
  }

  @Override
  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

  }

  @Override
  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
  }

  @Override
  public void afterTextChanged(Editable editable) {
      editText.removeTextChangedListener(this);

      //String s = editText.getText().toString();
      String s = editable.toString();
      s = G.converToEn(s);
      s = s.replace(",", "");
      s = s.replace("٬", "");
      if (s.length() > 0) {
        DecimalFormat sdd = new DecimalFormat("#,###");
        Double doubleNumber = Double.parseDouble(s);
        String format = sdd.format(doubleNumber);
        editText.setText(format);
        editText.setSelection(format.length());
      } else {
      }

      if (editText.getId() == R.id.edt_km_now) {
          G.preference.edit().putBoolean("ChangeAvgKm", true).apply();

          Log.d("Guid", "afterTextChanged:ChangeAvgKm " + G.preference.getBoolean("ChangeAvgKm", false));
        if (G.preference.getBoolean("ChangeAvgKm", false)) {
          if (editable.toString().length() > 0) {
            int edt_km_nowX = Integer.parseInt(G.converToEn(editable.toString()));
            editText1.setText((edt_km_nowX + G.preference.getInt("AvgKm", 0)) + "");
            G.preference.edit().putBoolean("ChangeAvgKm", false).apply();
          }
        }
      }
      editText.addTextChangedListener(this);

    }


}
