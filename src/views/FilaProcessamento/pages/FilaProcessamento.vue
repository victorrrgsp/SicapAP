<template>
  <div class="overflow-auto">
    <b-card no-body class="mb-5 mt-5">
      <b-card-header header-tag="nav">
        <b-nav card-header tabs>
          <b-nav-item active>Fila de Processamento - Em andamento</b-nav-item>
        </b-nav>
      </b-card-header>
      <b-card-body v-if="this.fila === []">
        {{ "Sem processos" }}
      </b-card-body>
      <b-card-body v-else>
        <b-table
          striped
          hover
          responsive
          sticky-header="700px"
          id="table"
          :items="fila"
          :fields="items2"
          :per-page="perPage"
          :current-page="currentPage"
          aria-controls="table"
          small
        >
          <template #table-busy>
            <div class="text-center text-danger my-2">
              <b-spinner class="align-middle"></b-spinner>
              <strong>Loading...</strong>
            </div>
          </template>
          
        </b-table>
       <p v-if="(fila.length == 0)" class="text-danger"> Sem processos!</p>
      </b-card-body>
    </b-card>
    <b-card no-body>
      <b-card-header header-tag="nav">
        <b-nav card-header tabs>
          <b-nav-item active>Fila de Processamento - Finalizados</b-nav-item>
        </b-nav>
      </b-card-header>
      <b-row>
      <b-col  >
         <p align="left" >
              <b-form-group
                label-for="filter-input"
                label-cols-sm="3"
                label-align-sm="right"
                label-size="sm"
                class="mb-0"
              >
                <b-input-group size="sm">
                        <b-form-input
                          id="filter-input"
                          v-model="filter"
                          type="search"
                          placeholder="Pesquise aqui..."
                        >
                        </b-form-input>

                        <b-input-group-append>
                          <b-button :disabled="!filter" @click="filter = ''">Limpar</b-button>
                        </b-input-group-append>
                </b-input-group>
              </b-form-group>
          </p>
      </b-col> 

      <b-col >
        <p align="right" class="pesquisa_select" >
              <b>Exercicio:</b> &nbsp; <b-form-select class="select-selected" v-model="formdata.exercicio" :options="formdata.exercicios"> </b-form-select>
              &nbsp;   
               <b>Remessa:</b> &nbsp; <b-form-select class="select-selected" v-model="formdata.remessa" :options="formdata.remessas"> </b-form-select>
              &nbsp;   
              <b-button @click="pesquisarRemesssa" pill variant="success" size="sm"> Pesquisar </b-button>
        </p>
      </b-col>


     </b-row>


      <b-card-body>
        <b-table
        :busy="isBusy"
          striped
          hover
          responsive
          sticky-header="700px"
          id="my-table"
          :filter="filter"
          :items="processos"
          :fields="items"
          :per-page="perPage"
          :current-page="currentPage"
          aria-controls="my-table"
          :tbody-tr-class="rowClass"
         small
        >
          <template #table-busy>
            <div class="text-center text-danger my-2">
              <b-spinner class="align-middle"></b-spinner>
              <strong>Loading...</strong>
            </div>
          </template>

          <template #cell(status)="data">
            <b-icon  v-if= "statusIcon( data.value ) == 'dash-circle'" class="h2 mb-1"
                :icon="statusIcon( data.value )" 
               
                 variant="danger"
                cursor= "pointer" 
                title="Assinaturas"
                @click="info(data.item, data.index, $event.target)" pill 
               
                size="sm">
            </b-icon> 
            <b-icon  v-else class="h2 mb-2"
                :icon="statusIcon( data.value )" 
               
                 variant="success"
                cursor= "pointer" 
                title="Assinaturas"
                @click="info(data.item, data.index, $event.target)" pill 
               
                size="sm">
            </b-icon>
    
          </template>
        </b-table>
      <b-icon  class="h1 mb-2" icon="check"  variant="success"> </b-icon> &nbsp;    &nbsp;Enviado  &nbsp;   &nbsp;    &nbsp;
      <b-icon  class="h3 mb-1" icon="dash-circle"  variant="danger"> </b-icon> &nbsp;    &nbsp;Cancelado 
      </b-card-body>
    </b-card>
    <!-- <b-pagination
      v-model="currentPage"
      :total-rows="rows"
      :per-page="perPage"
      aria-controls="my-table"
    ></b-pagination>
     -->
  </div>
</template>

<script>
import { api } from "@/plugins/axios";

export default {
  data() {
    return {
      aprovado: "aprovado",
      isBusy: true,
      perPage: 5000,
      currentPage: 1,
      filter: null,
      processos: [],
      formdata:{
               exercicio: null,
               exercicios: [
                            { value: null, text: 'Todos' },
                            { value: 2021, text: '2021' },
                           ],
               remessa: null,
               remessas: [

                            { value: null, text: 'Todos' },
                            { value: 1, text: '1' },
                            { value: 2, text: '2' },
                            { value: 3, text: '3' },
                            { value: 4, text: '4' },
                            { value: 5, text: '5' },
                            { value: 6, text: '6' },
                            { value: 7, text: '7' },
                            { value: 8, text: '8' },
                            { value: 9, text: '9' },
                            { value: 10, text: '10' }
                          ]
            },
      fila: [],
      items: [
        {
          key: "nome",
          label: "Unidade Gestora",
          sortable: true,
          //tdClass: 'fonteLinhasLeft'
          // formatter: 'todasMaiusculas'
        },

        {
          key: "exercicio",
          label: "Exercicio",
          sortable: true,
          tdClass: 'fonteLinhas'
        },
        {
          key: "remessa",
          label: "Remessa",
          sortable: true,
          tdClass: 'fonteLinhas'
        },

        {
          key: "dataEnvio",
          label: "Data Envio",
          formatter: "formatarData",
          sortable: false,
          tdClass: 'fonteLinhas'
        },
        {
          key: "dataProcessamento",
          label: "Data Procesamento",
          formatter: "formatarData",
          sortable: true,
          tdClass: 'fonteLinhas'
        },
        {
        key: 'status',
        label:'Status',
        sortable: false,
        tdClass: 'fonteLinhas'

        },
      ],
      items2: [
        {
          key: "nome",
          label: "Unidade Gestora",
          sortable: true,
          // formatter: 'todasMaiusculas'
        },

        {
          key: "exercicio",
          label: "Exercicio",
          sortable: true,
        },
        {
          key: "remessa",
          label: "Remessa",
          sortable: true,
        },

        {
          key: "dataEnvio",
          label: "Data Envio",
          sortable: false,
          formatter: "formatarData",
        },
        {
          key: "posicao",
          label: "Posição",
          sortable: true,
        },
        {
          key: "status",
          label: "Status",
          sortable: false,
          //formatter: "rowClass"
        },
      ],
    };
  },
  mounted() {
    this.isBusy = true
    this.FindAll();
    //  setTimeout(() =>{// aguarda com spinner antes da pesquisa aparecer na pesquisa inicial
    //               this.isBusy = false
    //               }, 1.0*1000)
    
   // this.isBusy = false
    
    //this.pesquisar()
      
    setInterval(this.readForms, 1000);
  
  },
  methods: {
    pesquisarRemesssa() {
      api.get("filaProcessamento/processos").then((resp) => {
        
        this.processos = resp.data.filter(p => {return !( p.remessa !== this.formdata.remessa && p.exercicio !== this.formdata.exercicio)} );
      });
    },
    FindAll() {
      api.get("filaProcessamento/processos").then((resp) => {
        this.processos = resp.data;  
        this.isBusy = false        
      });
    },
    async pesquisar() {
      this.isBusy = !this.isBusy //loading 
      this.FindAll()
      this.isBusy = false
    },

    readForms() {
      api.get("filaProcessamento/fila").then((resp) => { 
        this.fila = resp.data;
      });
      
    },
    formatarData: function (value) {
      if (value === null) {
        return null;
      }
      return new Date(value).toLocaleString("pt-BR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      });
    },

    // FormatarStatus: function (value) {
    //   if (value === "ok") {
    //     return;
    //   }
    // },
    statusIcon(label){
      
        if(label === "ok") return 'check'
        else if (label === 'mapear erro') return 'dash-circle'
    },
     rowClass(item, type) {
        if (!item || type !== 'row') return
        if (item.status === 'mapear erro') return 'check'
        else if (item.status === 'ok')  return 'x'
      }
  },

  computed: {
    rows() {
      return this.processos.length;
    },
  },
};
</script>
<style >

.fonteLinhas {
   font-size:14px;
   text-align: center
}
.fonteLinhasLeft {
   font-size:14px;

}
.select-selected {
  border-color: black;
  border: 6px solid;
}

.select-selected.select-arrow-active:after {
  border-color: black;
  top: 7px;
}
.select-items div,
.select-selected {
  color: black;
  padding: 8px 16px;
  border: 1px solid;
  border-color: rgba(0, 0, 0, 0.2);
  cursor: pointer;
}

.select-items {
  position: absolute;

  top: 100%;
  left: 0;
  right: 0;
  z-index: 99;
}

.select-hide {
  display: none;
}
.pesquisa_select{

  position: relative;
  margin-top: 20px;

}
.select-items div:hover,
.same-as-selected {
  background-color: rgba(0, 0, 0, 0.1);
}
@mixin flex-center($columns: false) {
  display: flex;
  align-items: center;
  justify-content: center;
  @if $columns {
    flex-direction: column;
  }
}

</style>
