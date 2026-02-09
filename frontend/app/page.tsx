"use client";

import Link from "next/link";
import Image from "next/image";
import { useState } from "react";
import { Button } from "@/components/ui/button";

export default function LandingPage() {
  const [activeTab, setActiveTab] = useState<"transportadora" | "caminhoneiro">("transportadora");

  return (
    <div className="flex min-h-screen flex-col font-sans">
      {/* Header/Nav can go here if needed, usually separate component */}
      
      {/* Hero Section */}
      <section className="relative h-screen flex items-center justify-center text-white overflow-hidden">
        <video
          autoPlay
          muted
          loop
          playsInline
          className="absolute inset-0 w-full h-full object-cover z-0"
          src="https://fretemais.com/images/video-intro.mp4"
        />
        <div className="absolute inset-0 bg-black/50 z-10" />
        
        <div className="relative z-20 text-center container mx-auto px-4">
          <h1 className="text-4xl md:text-6xl font-bold mb-6">
            Conectamos Transportadoras, Empresas e Caminhoneiros
            <br />
            <span className="text-gray-300 text-2xl md:text-4xl font-normal block mt-2">
              Sem intermediários.
            </span>
          </h1>
          <p className="text-lg md:text-xl mb-8 max-w-2xl mx-auto">
            Tudo que você precisa na palma da sua mão.
            Nós conectamos transportadoras e caminhoneiros
            diretamente, sem estresse e perda de tempo.
          </p>
          <div className="flex flex-col md:flex-row justify-center gap-4">
            <Button asChild size="lg" className="bg-[#f08518] hover:bg-[#d6730f] text-white">
               <Link href="/auth/login">Sou Transportadora</Link>
            </Button>
            <Button asChild size="lg" variant="outline" className="text-white border-white hover:bg-white/20">
              <Link href="/auth/login">Sou Motorista</Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-white">
        <div className="container mx-auto px-4">
            {/* Feature 1 */}
            <div className="flex flex-col md:flex-row items-center mb-20">
                <div className="md:w-1/2 mb-8 md:mb-0 md:pr-12">
                    <h2 className="text-4xl font-bold mb-4">Deixe que nós fazemos o trabalho pesado</h2>
                    <p className="text-lg text-gray-600">
                      Nosso sistema conecta transportadoras, empresas e caminhoneiros de uma maneira inteligente e eficiente.
                    </p>
                </div>
                <div className="md:w-1/2">
                    <img src="https://fretemais.com/images/frte-mais-features-01.gif" alt="Feature 1" className="w-full rounded-lg shadow-lg" />
                </div>
            </div>

            {/* Feature 2 */}
            <div className="flex flex-col md:flex-row-reverse items-center mb-20">
                <div className="md:w-1/2 mb-8 md:mb-0 md:pl-12">
                    <h2 className="text-4xl font-bold mb-4">Fique por dentro de tudo</h2>
                    <p className="text-lg text-gray-600">
                      Todos os documentos, requisitos, e informações relevantes são facilmente acessíveis e sem burocracias.
                    </p>
                </div>
                <div className="md:w-1/2">
                    <img src="https://fretemais.com/images/frte-mais-features-02.gif" alt="Feature 2" className="w-full rounded-lg shadow-lg" />
                </div>
            </div>

             {/* Feature 3 */}
             <div className="flex flex-col md:flex-row items-center mb-20">
                <div className="md:w-1/2 mb-8 md:mb-0 md:pr-12">
                    <h2 className="text-4xl font-bold mb-4">Fácil, seguro e rápido</h2>
                    <p className="text-lg text-gray-600">
                        Nosso sistema deixa o processo o mais fácil e seguro o possível, para que você possa se focar no que importa.
                    </p>
                </div>
                <div className="md:w-1/2">
                    <img src="https://fretemais.com/images/frte-mais-features-03.gif" alt="Feature 3" className="w-full rounded-lg shadow-lg" />
                </div>
            </div>
            
             {/* Feature 4 */}
             <div className="flex flex-col md:flex-row-reverse items-center">
                <div className="md:w-1/2 mb-8 md:mb-0 md:pl-12">
                    <h2 className="text-4xl font-bold mb-4">Controle do começo ao fim</h2>
                    <p className="text-lg text-gray-600">
                        Não importa se você é caminhoneiro, empresa ou transportadora. Você tem controle total, desde a busca de um novo frete, até negociação de valores.
                    </p>
                </div>
                <div className="md:w-1/2">
                    <img src="https://fretemais.com/images/frte-mais-features-04.gif" alt="Feature 4" className="w-full rounded-lg shadow-lg" />
                </div>
            </div>
        </div>
      </section>

      {/* Services Section */}
      <section className="py-20 bg-gray-50">
        <div className="container mx-auto px-4 text-center">
             <h2 className="text-4xl font-bold mb-12 max-w-4xl mx-auto">
               Com nosso sistema, tanto transportadoras, como empresas e caminhoneiros têm controle total da operação.
            </h2>
            
            <div className="flex justify-center gap-4 mb-12">
                <button 
                    onClick={() => setActiveTab('transportadora')}
                    className={`px-8 py-3 rounded-full text-lg font-semibold transition-all ${activeTab === 'transportadora' ? 'bg-[#f08518] text-white shadow-lg' : 'bg-white text-gray-600 hover:bg-gray-100'}`}
                >
                    Para Transportadoras
                </button>
                <button 
                    onClick={() => setActiveTab('caminhoneiro')}
                    className={`px-8 py-3 rounded-full text-lg font-semibold transition-all ${activeTab === 'caminhoneiro' ? 'bg-[#f08518] text-white shadow-lg' : 'bg-white text-gray-600 hover:bg-gray-100'}`}
                >
                    Para Caminhoneiros
                </button>
            </div>

            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8 text-left">
                {activeTab === 'transportadora' ? (
                    <>
                        <ServiceCard 
                            title="Poste um novo frete"
                            desc="Em poucos cliques, você cadastra sua carga, coloca a forma de pagamento, valor que quer pagar e escolhe o tipo de transportador que deseja."
                            img="https://fretemais.com/images/servicos/img-transportadoras-01.jpg"
                        />
                         <ServiceCard 
                            title="Encontre o caminhoneiro ideal"
                            desc="Com um clique, sua proposta chega a diversos caminhoneiros que atendem suas exigências."
                            img="https://fretemais.com/images/servicos/img-transportadoras-02.jpg"
                        />
                         <ServiceCard 
                            title="Negocie"
                            desc="Receba o retorno de diversos caminhoneiros interessando na sua carga e escolha a melhor proposta."
                            img="https://fretemais.com/images/servicos/img-transportadoras-03.jpg"
                        />
                    </>
                ) : (
                    <>
                        <ServiceCard 
                            title="Faça checkin e receba propostas"
                            desc="São inúmeras empresas no Brasil utilizando FRETE + e buscando o motorista ideal para a sua carga."
                            img="https://fretemais.com/images/servicos/img-caminhoneiros-01.jpg"
                        />
                         <ServiceCard 
                            title="Negocie o valor do frete"
                            desc="O caminhoneiro aqui tem vez, sem intermediário, você se torna dono do seu negócio."
                            img="https://fretemais.com/images/servicos/img-caminhoneiros-02.jpg"
                        />
                         <ServiceCard 
                            title="Colete a carga"
                            desc="Agora que já fechamos o frete, é só ir ao local de coleta, no dia e hora marcado."
                            img="https://fretemais.com/images/servicos/img-caminhoneiros-03.jpg"
                        />
                    </>
                )}
            </div>
             <div className="mt-12">
                 <Button asChild size="lg" className="bg-[#f08518] hover:bg-[#d6730f] text-white">
                   <Link href="/auth/login">Faça o seu cadastro grátis</Link>
                </Button>
            </div>
        </div>
      </section>

      {/* CTA Download App */}
      <section className="py-20 bg-[#2b2b2b] text-white">
          <div className="container mx-auto px-4">
              <div className="flex flex-col md:flex-row items-center justify-between">
                  <div className="mb-8 md:mb-0">
                      <h2 className="text-4xl font-bold mb-4">Comece a descomplicar por aqui:</h2>
                  </div>
                  <div className="flex gap-8">
                       <div className="text-center">
                            <img src="https://fretemais.com/images/svg/img-monitor.svg" className="h-24 mx-auto mb-4" alt="Web" />
                            <Link href="/auth/login" className="inline-block border border-white px-6 py-2 rounded hover:bg-white hover:text-black transition">
                                Cadastre-se
                            </Link>
                       </div>
                       <div className="text-center">
                            <img src="https://fretemais.com/images/svg/img-smartphone.svg" className="h-24 mx-auto mb-4" alt="App" />
                            <span className="inline-block border border-white px-6 py-2 rounded opacity-50 cursor-not-allowed">
                                Baixe o App
                            </span>
                       </div>
                  </div>
              </div>
          </div>
      </section>

      {/* Footer */}
      <footer className="bg-black text-white py-12">
        <div className="container mx-auto px-4">
            <div className="flex flex-col md:flex-row justify-between items-center mb-8">
                <div className="mb-4 md:mb-0">
                   <span className="text-2xl font-bold">Frete Mais+</span>
                </div>
                <div className="flex gap-8">
                    <Link href="#" className="hover:text-gray-300">Sobre</Link>
                    <Link href="#" className="hover:text-gray-300">Termos & Segurança</Link>
                    <Link href="#" className="hover:text-gray-300">Fale Conosco</Link>
                </div>
            </div>
            <div className="border-t border-gray-800 pt-8 text-center text-sm text-gray-400">
                Frete Mais™ 2026 Todos os direitos reservados.
            </div>
        </div>
      </footer>
    </div>
  );
}

function ServiceCard({ title, desc, img }: { title: string, desc: string, img: string }) {
    return (
        <div className="bg-white rounded-lg overflow-hidden shadow-md hover:shadow-xl transition-shadow">
            <div className="h-48 overflow-hidden">
                <img src={img} alt={title} className="w-full h-full object-cover transition-transform hover:scale-105" />
            </div>
            <div className="p-6">
                <h3 className="text-xl font-bold mb-3">{title}</h3>
                <p className="text-gray-600 text-sm">{desc}</p>
            </div>
        </div>
    )
}

