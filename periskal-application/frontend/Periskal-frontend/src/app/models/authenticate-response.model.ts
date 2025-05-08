import { Skipper } from "./Skipper.model";

export interface AuthenticateResponse {
    id: string;
    username: string;
    firstname: string;
    lastname: string;
    role: string;
    shipname: string;
    products: string[];
    loginResult: string;
    token: string;
    skippers?: Skipper[]; // Optional property, only present for fleetmanager?
  }
    