export const logger = {
  debug: (...args: unknown[]) => console.debug('[cyfarmsched]', ...args),
  info: (...args: unknown[]) => console.info('[cyfarmsched]', ...args),
  warn: (...args: unknown[]) => console.warn('[cyfarmsched]', ...args),
  error: (...args: unknown[]) => console.error('[cyfarmsched]', ...args),
};
