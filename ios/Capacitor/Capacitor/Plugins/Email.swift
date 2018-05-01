import Foundation
import MessageUI

@objc(CAPSharePlugin)
public class CAPSharePlugin : CAPPlugin, MFMailComposeViewControllerDelegate {
  @objc func send(_ call: CAPPluginCall) -> MFMailComposeViewController {
    let mail = MFMailComposeViewController()
    if !mail.canSendMail() {
      call.error("No email client found.")
      return
    }
    
    mail.mailComposeDelegate = self

    if let to = call.options["to"] as? String[] {
      mail.setToRecipients(to)
    }

    if let message = call.options["message"] as? String {
      mail.setMessageBody(message, isHTML: false)
    }
    
    self.bridge.viewController.present(mail, animated: true)
  }

  @obj func available(_ call: CAPPluginCall) {
    call.success([
      "value": MFMailComposeViewController.canSendMail()
    ])
  }
}