import { Chapter } from "./chapter.model"

export interface Module {
    id: number
    nameModuleEn: String
    descriptionModuleEn: String
    nameModuleNl: String
    descriptionModuleNl: String
    nameModuleFr: String
    descriptionModuleFr: String
    nameModuleDe: String
    descriptionModuleDe: String
    chapters: Chapter[]
}