import { Photo } from "./photo.model"
import { Video } from "./video.model"

export interface Chapter {
    id: number
    nameChapterEn: String
    descriptionChapterEn: String
    textChapterEn: string
    videoEn?: Video;  // English video

    nameChapterNl: String
    descriptionChapterNl: String
    textChapterNl: string
    videoNl?: Video;  // Dutch video

    nameChapterFr: String
    descriptionChapterFr: String
    textChapterFr: string
    videoFr?: Video;  // French video

    nameChapterDe: String
    descriptionChapterDe: String
    textChapterDe: string
    videoDe?: Video;  // German video

    photos: Photo[]

    // videoRemoteFilePath: string
    //todo: implement Photos_nl, Photos_fr and Photos_de
}