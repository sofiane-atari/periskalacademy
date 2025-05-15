import { Photo } from "./photo.model"
import { Video } from "./video.model"

export interface Chapter {
    id: number
    nameChapterEn: String
    descriptionChapterEn: String
    textChapterEn: string
    nameChapterNl: String
    descriptionChapterNl: String
    textChapterNl: string
    nameChapterFr: String
    descriptionChapterFr: String
    textChapterFr: string
    nameChapterDe: String
    descriptionChapterDe: String
    textChapterDe: string
    photos: Photo[]
    videoRemoteFilePath: string
    //todo: implement Photos_nl, Photos_fr and Photos_de
}