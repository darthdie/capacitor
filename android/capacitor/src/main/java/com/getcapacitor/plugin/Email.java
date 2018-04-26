package com.getcapacitor.plugin;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

@NativePlugin()
public class Email extends Plugin {

  @PluginMethod()
  public void send(PluginCall call) {
    JSArray to = call.getArray("to", new JSArray());
    JSArray cc = call.getArray("cc", new JSArray());
    JSArray bcc = call.getArray("bcc", new JSArray());
    JSArray attachments = call.getArray("attachments", new JSArray());
    String subject = call.getString("subject", "");
    String message = call.getString("message", "");
    
    final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    intent.setType("message/rfc822");
    intent.setData(Uri.parse("mailto"));

    intent.putExtra(Intent.EXTRA_SUBJECT, subject);

    if(Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL).matcher(message).matches()) {
      intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message));
      intent.setType("text/html");
    }
    else {
      intent.putExtra(Intent.EXTRA_TEXT, message);
      intent.setType("text/plain");
    }

    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    if(attachments.length() > 0) {
      ArrayList<Uri> attachmentUris = new ArrayList<>();

      try {
        for (String file : attachments.<String>toList()) {
          String appId = getAppId();
          File attachmentFile = new File(Uri.parse(file).getPath());

          Uri uri = FileProvider.getUriForFile(getActivity(), appId + ".fileprovider", attachmentFile);
          attachmentUris.add(uri);
        }
      } catch(org.json.JSONException ex) {
        call.error("Error processing provided attachments. Please make sure that they are valid and exist.", ex);
        return;
      }

      intent.putExtra(Intent.EXTRA_STREAM, attachmentUris);
    }

    try {
      intent.putExtra(Intent.EXTRA_EMAIL, to.<String>toList().toArray(new String[to.length()]));
      intent.putExtra(Intent.EXTRA_CC, cc.<String>toList().toArray(new String[to.length()]));
      intent.putExtra(Intent.EXTRA_BCC, bcc.<String>toList().toArray(new String[to.length()]));

      getActivity().startActivity(Intent.createChooser(intent, "Send Email"));
    } catch(org.json.JSONException ex) {
      call.error("Error processing provided emails. Please make sure that they are valid and strings.", ex);
    } catch (android.content.ActivityNotFoundException ex) {
      call.error("No email client was found.");
    }
  }

  @PluginMethod()
  public void available(PluginCall call) {
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "you@domain.com", null));

    JSObject ret = new JSObject();
    ret.put("value", getActivity().getPackageManager().queryIntentActivities(intent, 0).size() > 0);
    call.success(ret);
  }
 }