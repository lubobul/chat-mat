export function resolveErrorMessage(error: any): string{
    return error?.message || error?.error || error?.error?.message || error;
}
