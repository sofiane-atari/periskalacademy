import { Photo } from "./photo.model"

export interface Chapter {
    id: number
    nameChapter_en: String
    descriptionChapter_en: String
    textChapter_en: string
    nameChapter_nl: String
    descriptionChapter_nl: String
    textChapter_nl: string
    nameChapter_fr: String
    descriptionChapter_fr: String
    textChapter_fr: string
    nameChapter_de: String
    descriptionChapter_de: String
    textChapter_de: string
    photos: Photo[]
}