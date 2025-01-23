export function resolveErrorMessage(error: any): string{
    return error?.error?.error || error?.message || error?.error?.message || error;
}
