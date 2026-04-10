import { registerPlugin } from '@capacitor/core';

import type { Aria2Plugin } from './definitions';

const Aria2 = registerPlugin<Aria2Plugin>('Aria2', {
  web: () => import('./web').then((m) => new m.Aria2Web()),
});

export * from './definitions';
export { Aria2 };
