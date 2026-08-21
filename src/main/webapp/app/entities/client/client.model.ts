import { IAnimal } from 'app/entities/animal/animal.model';

export interface IClient {
  id: number;
  nom?: string | null;
  prenom?: string | null;
  adresse?: string | null;
  telephone?: string | null;
  email?: string | null;
  animals?: IAnimal[] | null; // <- champs requis par le template HTML
}

export type NewClient = Omit<IClient, 'id'> & { id: null };
