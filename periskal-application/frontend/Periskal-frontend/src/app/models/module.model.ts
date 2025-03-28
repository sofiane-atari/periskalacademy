import { Chapter } from "./chapter.model"

export interface Module {
    id: number
    nameModule: String
    descriptionModule: String
    chapters: Chapter[]
}