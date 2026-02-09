"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Plus, Pencil, Trash2, LogOut } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";

import { http } from "@/lib/http-client";
import { clearToken, getToken } from "@/lib/auth";
import type { Driver, DriverInput } from "@/types/driver";

export default function DashboardPage() {
  const router = useRouter();
  const [drivers, setDrivers] = useState<Driver[]>([]);
  const [loading, setLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [currentDriver, setCurrentDriver] = useState<DriverInput | null>(null);

  // Form states
  const [formData, setFormData] = useState<DriverInput>({
    name: "",
    email: "",
    phone: "",
    cpf: "",
    cnh: "",
    city: "",
    state: "",
    vehicleTypes: [],
    available: true,
  });

  const [filters, setFilters] = useState({
    text: "",
    city: "",
    state: "",
    vehicleType: "ALL"
  });

  useEffect(() => {
    const token = getToken();
    if (!token) {
      router.push("/auth/login");
      return;
    }
    fetchDrivers();
  }, [router]);

  const fetchDrivers = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.text) params.append("text", filters.text);
      if (filters.city) params.append("city", filters.city);
      if (filters.state) params.append("state", filters.state);
      if (filters.vehicleType && filters.vehicleType !== "ALL") params.append("vehicles", filters.vehicleType);

      const response = await http.get(`/api/drivers?${params.toString()}`);
      if (Array.isArray(response.data)) {
         setDrivers(response.data);
      } else if (response.data.content) {
         setDrivers(response.data.content);
      } else {
        setDrivers([]);
      }
    } catch (error) {
      console.error("Failed to fetch drivers", error);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      setFilters({ ...filters, [e.target.id]: e.target.value });
  };

  const applyFilters = (e: React.FormEvent) => {
      e.preventDefault();
      fetchDrivers();
  }

  const handleDelete = async (id: string) => {
    if (!confirm("Tem certeza que deseja excluir?")) return;
    try {
      await http.delete(`/api/drivers/${id}`);
      fetchDrivers();
    } catch (error) {
       console.error("Failed to delete", error);
       alert("Erro ao excluir motorista");
    }
  };

  const openNewDriver = () => {
    setFormData({
        name: "",
        email: "",
        phone: "",
        cpf: "",
        cnh: "",
        city: "",
        state: "",
        vehicleTypes: [],
        available: true,
    });
    setCurrentDriver(null);
    setIsDialogOpen(true);
  };

  const openEditDriver = (driver: Driver) => {
    setFormData({ ...driver }); // copy
    setCurrentDriver(driver);
    setIsDialogOpen(true);
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (currentDriver && currentDriver.id) {
         await http.put(`/api/drivers/${currentDriver.id}`, formData);
      } else {
         await http.post("/api/drivers", formData);
      }
      setIsDialogOpen(false);
      fetchDrivers();
    } catch (error) {
        console.error("Error saving", error);
        alert("Erro ao salvar");
    }
  };

  const handleLogout = () => {
      clearToken();
      router.push("/auth/login");
  }

  const toggleVehicleType = (type: "CAR" | "MOTORCYCLE" | "TRUCK") => {
    setFormData(prev => {
        const current = prev.vehicleTypes || [];
        if (current.includes(type)) {
            return { ...prev, vehicleTypes: current.filter(t => t !== type) };
        } else {
            return { ...prev, vehicleTypes: [...current, type] };
        }
    });
  };

  return (
    <div className="container mx-auto py-10 min-h-screen bg-white text-black">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Gestão de Motoristas</h1>
        <div className="flex gap-4">
             <Button onClick={openNewDriver}>
              <Plus className="mr-2 h-4 w-4" /> Novo Motorista
            </Button>
            <Button variant="outline" onClick={handleLogout}>
                <LogOut className="mr-2 h-4 w-4" /> Sair
            </Button>
        </div>
      </div>

      <div className="bg-white p-4 rounded-md border mb-6 shadow-sm">
          <form onSubmit={applyFilters} className="grid grid-cols-1 md:grid-cols-5 gap-4 items-end">
              <div>
                  <Label htmlFor="text">Busca (Nome, CPF, Email)</Label>
                  <Input id="text" placeholder="Digite para buscar..." value={filters.text} onChange={handleFilterChange} />
              </div>
              <div>
                  <Label htmlFor="city">Cidade</Label>
                  <Input id="city" placeholder="Ex: São Paulo" value={filters.city} onChange={handleFilterChange} />
              </div>
              <div>
                  <Label htmlFor="state">Estado</Label>
                  <Input id="state" placeholder="UF" maxLength={2} value={filters.state} onChange={handleFilterChange} />
              </div>
              <div>
                  <Label htmlFor="vehicleType">Veículo</Label>
                  <select
                    id="vehicleType"
                    className="flex h-10 w-full items-center justify-between rounded-md border border-input bg-white text-black px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                    value={filters.vehicleType}
                    onChange={handleFilterChange}
                  >
                      <option value="ALL">Todos</option>
                      <option value="CAR">Carro</option>
                      <option value="MOTORCYCLE">Moto</option>
                      <option value="TRUCK">Caminhão</option>
                  </select>
              </div>
              <Button type="submit" variant="secondary">Filtrar</Button>
          </form>
      </div>

      <div className="rounded-md border p-4">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Nome</TableHead>
              <TableHead>CPF</TableHead>
              <TableHead>Cidade/UF</TableHead>
              <TableHead>Veículos</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Ações</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
                <TableRow>
                    <TableCell colSpan={6} className="text-center">Carregando...</TableCell>
                </TableRow>
            ) : drivers.length === 0 ? (
                <TableRow>
                     <TableCell colSpan={6} className="text-center">Nenhum motorista encontrado.</TableCell>
                </TableRow>
            ) : (
                drivers.map((driver) => (
                  <TableRow key={driver.id}>
                    <TableCell className="font-medium">{driver.name}</TableCell>
                    <TableCell>{driver.cpf}</TableCell>
                    <TableCell>{driver.city} / {driver.state}</TableCell>
                    <TableCell>{driver.vehicleTypes?.join(", ")}</TableCell>
                    <TableCell>{driver.available ? "Disponível" : "Indisponível"}</TableCell>
                    <TableCell className="text-right">
                       <Button variant="ghost" size="icon" onClick={() => openEditDriver(driver)}>
                           <Pencil className="h-4 w-4" />
                       </Button>
                       <Button variant="ghost" size="icon" onClick={() => handleDelete(driver.id)} className="text-red-500">
                           <Trash2 className="h-4 w-4" />
                       </Button>
                    </TableCell>
                  </TableRow>
                ))
            )}
          </TableBody>
        </Table>
      </div>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>{currentDriver ? "Editar Motorista" : "Novo Motorista"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleFormSubmit} className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="name" className="text-right">Nome</Label>
              <Input id="name" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} className="col-span-3" required />
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="email" className="text-right">Email</Label>
              <Input id="email" type="email" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} className="col-span-3" />
            </div>
             <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="cpf" className="text-right">CPF</Label>
              <Input id="cpf" value={formData.cpf} onChange={e => setFormData({...formData, cpf: e.target.value})} className="col-span-3" />
            </div>
             <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="phone" className="text-right">Telefone</Label>
              <Input id="phone" value={formData.phone} onChange={e => setFormData({...formData, phone: e.target.value})} className="col-span-3" />
            </div>
             <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="city" className="text-right">Cidade</Label>
              <Input id="city" value={formData.city} onChange={e => setFormData({...formData, city: e.target.value})} className="col-span-3" />
            </div>
             <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="state" className="text-right">Estado</Label>
              <Input id="state" value={formData.state} maxLength={2} onChange={e => setFormData({...formData, state: e.target.value})} className="col-span-3" />
            </div>

            <div className="grid grid-cols-4 items-start gap-4">
              <Label className="text-right mt-2">Veículos</Label>
              <div className="col-span-3 space-y-2">
                 <div className="flex items-center space-x-2">
                    <Checkbox id="vh-car" checked={formData.vehicleTypes?.includes('CAR')} onCheckedChange={() => toggleVehicleType('CAR')} />
                    <label htmlFor="vh-car">Carro</label>
                 </div>
                 <div className="flex items-center space-x-2">
                    <Checkbox id="vh-moto" checked={formData.vehicleTypes?.includes('MOTORCYCLE')} onCheckedChange={() => toggleVehicleType('MOTORCYCLE')} />
                    <label htmlFor="vh-moto">Moto</label>
                 </div>
                 <div className="flex items-center space-x-2">
                    <Checkbox id="vh-truck" checked={formData.vehicleTypes?.includes('TRUCK')} onCheckedChange={() => toggleVehicleType('TRUCK')} />
                    <label htmlFor="vh-truck">Caminhão</label>
                 </div>
              </div>
            </div>

            <DialogFooter>
              <Button type="submit">Salvar</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
