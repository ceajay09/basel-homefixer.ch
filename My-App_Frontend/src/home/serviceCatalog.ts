// Shared identifiers and symbols; translated content lives in the DE/EN locale files.
export const serviceIcons = { kitchen: '🍳', bathroom: '🚿', heating: '🔥', 'windows-doors': '🪟', electrical: '⚡', 'smart-home': '🏠', energy: '💡', consulting: '📋' } as const;
export type ServiceId = keyof typeof serviceIcons;
export const homeServiceIds: readonly ServiceId[] = ['kitchen', 'energy', 'heating', 'smart-home', 'consulting'];
export interface ServiceDetailStep { title: string; items: string[]; }
export interface ServiceContent { id: ServiceId; title: string; description: string; features: string[]; details: ServiceDetailStep[]; }
