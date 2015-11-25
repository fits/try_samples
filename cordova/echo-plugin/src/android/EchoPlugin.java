package sample;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

public class EchoPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, 
                    CallbackContext callbackContext) throws JSONException {

        if (action.equals("showMessage")) {
            String msg = args.getString(0);

            if (msg != null) {
                callbackContext.success(msg);

                showMessage(msg);
            }
            else {
                callbackContext.error("message is null");
            }

            return true;
        }

        return false;
    }

    private void showMessage(String msg) {

        android.widget.Toast.makeText(
            cordova.getActivity(),
            msg,
            1
        ).show();
    }
}
