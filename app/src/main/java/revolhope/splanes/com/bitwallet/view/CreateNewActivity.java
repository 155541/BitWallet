package revolhope.splanes.com.bitwallet.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import revolhope.splanes.com.bitwallet.R;

public class CreateNewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);


        TextView textView_Id = findViewById(R.id.textView_Id);

        EditText editText_Account = findViewById(R.id.editText_Account);
        EditText editText_URL = findViewById(R.id.editText_URL);
        EditText editText_User = findViewById(R.id.editText_User);
        EditText editText_Password = findViewById(R.id.editText_Password);

        Button btGenerate = findViewById(R.id.btGenerate);

        CheckBox checkBox_Expire = findViewById(R.id.checkBox_Expire);
        TextView textView_ExpireDate = findViewById(R.id.textView_ExpireDate);

        EditText editText_Brief = findViewById(R.id.editText_Brief);

        Button btCreate = findViewById(R.id.btCreate);
        Button btCancel = findViewById(R.id.btCancel);
    }
}
