export interface Certificate {
    id?: number;
    name: string;
    issueDate: string;
    expiryDate: string;
    training: string;
    readyForDownload: boolean;
    userId?: string;
}
