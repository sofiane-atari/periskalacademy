import { Module } from "./module.model"

export interface Training {
    id: number
    product_code: String
    name_en: String
    name_nl: String
    name_fr: String
    name_de: String
    description_en: String
    description_nl: String
    description_fr: String
    description_de: String
    modules: Module[]
}