<template>
  <div class="overflow-auto">
    
 <b-card no-body class="mb-5">
    <b-card-header header-tag="nav">
      <b-nav card-header tabs>
        <b-nav-item active>Fila de Processamento</b-nav-item>
      
      </b-nav>
    </b-card-header>
     <b-card-body v-if="this.fila === []">
       {{'Sem processos'}}
     </b-card-body>
<b-card-body v-else>
<b-table striped hover responsive sticky-header="700px"
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
 </b-card-body>
  </b-card>
  <b-card no-body>
    <b-card-header header-tag="nav">
      <b-nav card-header tabs>
        <b-nav-item active>Fila de Processamento</b-nav-item>
      
      </b-nav>
    </b-card-header>
     <b-card-body>

    <b-table striped hover responsive sticky-header="700px"
      id="my-table"
      
      :items="tableData"
       :fields="items" 
      :per-page="perPage"
      :current-page="currentPage"
      aria-controls="my-table"
      small
    >
      <template #table-busy>
              <div class="text-center text-danger my-2">
                <b-spinner class="align-middle"></b-spinner>
                <strong>Loading...</strong>
              </div>
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
import { mapActions, mapState } from 'vuex'
import { api } from '@/plugins/axios'

export default {
  data() {
    return {
      isBusy: true,
      perPage: 5000,
      currentPage: 1,
      fila:[],
      items: [
                     
                      {
                        key: 'nome',
                        label:'Unidade Gestora',
                        sortable: true
                       // formatter: 'todasMaiusculas'
                      },
                      
                      {
                         key: 'exercicio',
                         label:'Exercicio',
                         sortable: true
                      },
                      {
                         key: 'remessa',
                         label:'Remessa',
                         sortable: true
                      },   
                    
                      {
                          key: 'dataEnvio',
                          label:'Data Envio',
                           formatter: 'formatarData',
                          sortable: false,
                         
                      },
                      {
                          key: 'dataProcessamento',
                          label:'Data Procesamento',
                           formatter: 'formatarData',
                          sortable: true,
                         
                      },
                        //{
                        // key: 'status',
                        // label:'Status',
                        // sortable: false
                      //},
      ],
       items2: [
                     
                      {
                        key: 'nome',
                        label:'Unidade Gestora',
                        sortable: true
                       // formatter: 'todasMaiusculas'
                      },
                      
                      {
                         key: 'exercicio',
                         label:'Exercicio',
                         sortable: true
                      },
                      {
                         key: 'remessa',
                         label:'Remessa',
                         sortable: true
                      },   
                    
                      {
                          key: 'dataEnvio',
                          label:'Data Envio',
                          sortable: false,
                           formatter: 'formatarData',
                         
                      },
                      {
                          key: 'posicao',
                          label:'Posição',
                          sortable: true,
                         
                      },
                        {
                         key: 'status',
                         label:'Status',
                         sortable: false
                      },
      ],
    };
  },
  mounted() {
    this.FindAll();
   // this.FindFila();
    //this.readForms();
    setInterval(this.readForms,1000);
    
  },
  methods: {
    ...mapActions('remessas', ["FindAll"]),
  

  readForms() {
        api.get('filaProcessamento/fila').then(resp => {
        console.log("data", resp.data)
            this.fila = resp.data;
            
        })
      console.log(this.fila)


    },
     formatarData: function (value) {
                       if (value === null) { return null }
                      return new Date(value).toLocaleString('pt-BR', { year: 'numeric', month: '2-digit', day: '2-digit', hour:'2-digit', minute:'2-digit', second:'2-digit' })
                  },

      FormatarStatus: function (value){
                    if(value === 'ok'){
                      return 
                    }
      }
    
  },

  computed: {
    rows() {
                   return this.tableData.length
                 
                  
                  },
                   
                
                  ...mapState('remessas', ['tableData']),
                
                  
    
  },
};
</script>