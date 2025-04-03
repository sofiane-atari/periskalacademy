import { Photo } from "./photo.model"

export interface Chapter {
    id: number
    nameChapter_en: String
    descriptionChapter_en: String
    textChapter_en: String
    nameChapter_nl: String
    descriptionChapter_nl: String
    textChapter_nl: String
    nameChapter_fr: String
    descriptionChapter_fr: String
    textChapter_fr: String
    nameChapter_de: String
    descriptionChapter_de: String
    textChapter_de: String
    photos: Photo[]
}