export type VehicleType = "CAR" | "MOTORCYCLE" | "TRUCK";

export interface Driver {
  id: string;
  name: string;
  email: string;
  phone: string;
  cpf: string;
  cnh: string;
  city: string;
  state: string;
  vehicleTypes: VehicleType[];
  available: boolean;
}

export type DriverInput = Omit<Driver, "id"> & { id?: string };

export interface Page<T> {
  content: T[];
  totalElements?: number;
  totalPages?: number;
  number?: number;
  size?: number;
}

