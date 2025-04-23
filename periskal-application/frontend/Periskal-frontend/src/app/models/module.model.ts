import { Chapter } from "./chapter.model"

export interface Module {
    id: number
    nameModule_en: String
    descriptionModule_en: String
    nameModule_nl: String
    descriptionModule_nl: String
    nameModule_fr: String
    descriptionModule_fr: String
    nameModule_de: String
    descriptionModule_de: String
    chapters: Chapter[]
}