<template>
  <div class="overflow-auto">
    <b-card no-body class="mb-5 mt-5">
      <b-card-header header-tag="nav">
        <b-nav card-header tabs>
          <b-nav-item active>Fila de Processamento</b-nav-item>
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
          <template #cell(status)="data">
            <b-icon  v-if="(data.item.value == aprovado)"  :icon=da
                cursor= "pointer" 
                title="Status"
                @click="info(data.item, data.index, $event.target)" pill 
                variant="primary" 
                size="sm">
            </b-icon>
         &nbsp;   
       
        </template>
        </b-table>
       <p v-if="(fila.length == 0)" class="text-danger"> Sem processos!</p>
      </b-card-body>
    </b-card>
    <b-card no-body>
      <b-card-header header-tag="nav">
        <b-nav card-header tabs>
          <b-nav-item active>Fila de Processamento</b-nav-item>
        </b-nav>
      </b-card-header>
      <b-card-body>
        <b-table
        :busy="isBusy"
          striped
          hover
          responsive
          sticky-header="700px"
          id="my-table"
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
            <b-icon  v-if= "statusIcon( data.value ) == 'exclamation-triangle-fill'" class="h2 mb-1"
                :icon="statusIcon( data.value )" 
               
                 variant="warning"
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
      </b-card-body>
    </b-card>
    <b-pagination
      v-model="currentPage"
      :total-rows="rows"
      :per-page="perPage"
      aria-controls="my-table"
    ></b-pagination>
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
      processos: [],
      fila: [],
      items: [
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
          formatter: "formatarData",
          sortable: false,
        },
        {
          key: "dataProcessamento",
          label: "Data Procesamento",
          formatter: "formatarData",
          sortable: true,
        },
        {
        key: 'status',
        label:'Status',
        sortable: false
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
    this.FindAll();
     setTimeout(() =>{// aguarda com spinner antes da pesquisa aparecer na pesquisa inicial
                  this.isBusy = false
                  }, 1.0*1000)
        
    setInterval(this.readForms, 1000);
  },
  methods: {
    FindAll() {
      api.get("filaProcessamento/processos").then((resp) => {
       
        this.processos = resp.data;
      });
    },

    readForms() {
      api.get("filaProcessamento/fila").then((resp) => {
       if (resp.data.length ){
          console.log("data", resp.data);
       } 
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
        else if (label === 'mapear erro') return 'exclamation-triangle-fill'
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
