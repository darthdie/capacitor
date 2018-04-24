package com.getcapacitor.plugin;

import android.content.Intent;

import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin()
public class Share extends Plugin {

  @PluginMethod()
  public void send(PluginCall call) {
    //TODO: support attachments
    
    String to = call.getArray("to", []);
    String cc = call.getArray("cc", []);
    String bc = call.getArray("bc", []);
    String subject = call.getString("subject", "");
    String message = call.getString("message", "");
    
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("message/rfc822");
    intent.putExtra(Intent.EXTRA_EMAIL, to);
    intent.putExtra(Intent.EXTRA_CC, cc);
    intent.putExtra(Intent.EXTRA_BC, bc);
    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    intent.putExtra(Intent.EXTRA_TEXT, message);

    try {
      getActivity().startActivity(Intent.createChooser(intent, "Send Email"));
    } catch (android.content.ActivityNotFoundException ex) {
      call.error("No email client was found.");
    }
  }

  @PluginMethod()
  public void available(PluginCall call) {
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "you@domain.com", null));

    call.success(getActivity().getPackageManager().queryIntentActivities(intent, 0).size() > 0);
  }
 }
