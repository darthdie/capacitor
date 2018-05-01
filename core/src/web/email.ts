import { WebPlugin } from './index';

import {
  EmailPlugin,
  SendEmailOptions
} from '../core-plugin-definitions';

export class EmailPluginWeb extends WebPlugin implements EmailPlugin {
  constructor() {
    super({
      name: 'Email',
      platforms: ['web']
    });
  }

  send(options?: SendEmailOptions): Promise<void> {
    if (options) {
      return Promise.resolve();
    }
    return Promise.resolve();
  }

  available(): Promise<boolean> {
    return Promise.resolve(true);
  }
}

const Email = new EmailPluginWeb();

export { Email };
