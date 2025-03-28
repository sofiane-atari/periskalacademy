import { Photo } from "./photo.model"

export interface Chapter {
    id: number
    nameChapter: String
    descriptionChapter: String
    textChapter: String
    photos: Photo[]
}