import { Exam } from "./exam.model"
import { Module } from "./module.model"

export interface Training {
    id: number
    productCode: String
    nameEn: String
    nameNl: String
    nameFr: String
    nameDe: String
    descriptionEn: String
    descriptionNl: String
    descriptionFr: String
    descriptionDe: String
    modules: Module[]
    exam: Exam | null
}