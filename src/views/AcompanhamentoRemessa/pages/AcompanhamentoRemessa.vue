<template>
   <div>
<!-- {{ '2021-11-17 12:37:31.9100000' | formatarDataEntrada }} -->

            <b-col lg="6" class="my-1">
              <b-form-group
                label="Busca"
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
                    placeholder="Digite aqui..."
                  ></b-form-input>

                  <b-input-group-append>
                    <b-button :disabled="!filter" @click="filter = ''">Clear</b-button>
                  </b-input-group-append>
                </b-input-group>
              </b-form-group>
            </b-col> 


          <p align="right" >
              Exercicio: <b-form-select v-model="formdata.exercicio" :options="formdata.exercicios"></b-form-select>
              &nbsp;   
              Remessa:&nbsp;<b-form-select v-model="formdata.remessa" :options="formdata.remessas"></b-form-select>
              &nbsp;   &nbsp;    &nbsp;
              <b-button @click="pesquisarRemesssa"  pill variant="success"  size="sm">Pesquisar</b-button>
          </p>
 <!-- :tbody-tr-class="rowClass" -->
     <b-table striped hover responsive sticky-header="700px" 
         id="my-table"
       
        :busy="isBusy"
        :items="tableData" 
        :filter="filter"
        :fields="columns" 
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

          <template #cell(opcao)="data">
            <b-icon icon="pen-fill" 
                cursor= "pointer" 
                title="Assinaturas"
                @click="info(data.item, data.index, $event.target)" pill 
                variant="primary" 
                size="sm">
            </b-icon>
         &nbsp;   
       
        </template>
     </b-table>
<!-- Info modal -->
    <b-modal :id="infoModal.id" :title="infoModal.title" ok-only @hide="resetInfoModal" size="xl"  >
     
     <b-container fluid="xl">
      <b-row>
        <b-col><b>Gestor</b></b-col>
        <b-col><b>Responsável R.H.</b></b-col> 
        <b-col><b>Controle Interno</b></b-col>
      </b-row>

      <b-row class="fonteLinhas">
        <b-col>{{gestor}}</b-col>
        <b-col>{{rh}}</b-col> 
        <b-col>{{controleInterno}}</b-col>
      </b-row>

      <b-row class="fonteLinhas">
        <b-col>Data Assinatura: {{ dataAssinaturaGestor == "" ? '---' :  formatarData(dataAssinaturaGestor) }}</b-col>
        <b-col>Data Assinatura: {{ dataAssinaturaRh === "" ? '---' : formatarData(dataAssinaturaRh) }}</b-col> 
        <b-col>Data Assinatura: {{ dataAssinaturaCI === "" ? '---' : formatarData(dataAssinaturaCI) }}</b-col>
      </b-row>

     </b-container>

    </b-modal>

    <b-pagination
      v-model="currentPage"
      :total-rows="rows"
      :per-page="perPage"
      aria-controls="my-table"
    ></b-pagination>
    
    </div>


    
</template>


<script>
import {api} from '@/plugins/axios'

import { mapActions, mapState } from 'vuex'
import maskMixins from '@/helpers/mixins/mask'
export default {

        mixins:[maskMixins], 

        data () {
          return {
            isBusy: true,
            perPage: 325,
            currentPage: 1,
            filter: null,
            items:[],
          
            gestor: "",
            rh: "",
            controleInterno: "",
            dataAssinaturaGestor: "",
            dataAssinaturaRh: "",
            dataAssinaturaCI: "",

             infoModal: {
                        id: 'info-modal',
                        title: '',
                        content: ''
                       },
             columns:[  
                      {
                        key: 'nomeEntidade',
                        label:'Unidade Gestora',
                        sortable: true,
                        thStyle: { width: "45%",  },
                        tdClass: 'fonteLinhas'
                      },
                       {
                        key: 'cnpj',
                         label:'Cnpj',
                         sortable: false,
                         thStyle: { width: "10%" },
                         formatter: 'mascaraCnpj',
                         tdClass: 'fonteLinhas'
                      },
                      {
                         key: 'exercicio',
                         label:'Exercicio',
                         sortable: true,
                        tdClass: 'fonteLinhas'
                      },
                      {
                         key: 'remessa',
                         label:'Remessa',
                         sortable: true,
                        tdClass: 'fonteLinhas'
                      },   
                      {
                         key: 'relatoria',
                         label:'Relatoria',
                         sortable: false,
                        tdClass: 'fonteLinhas'
                      },
                      {
                        key:'contAssinaturas',
                        label:'Assinaturas',
                        sortable: true,
                        tdClass: 'fonteLinhas'
                      //  formatter: 'index'
                      },
                      {
                          key: 'dataEntrega',
                          label:'Data Entrega',
                          sortable: false,
                          formatter: 'formatarData',
                           thStyle: { width: "10%",  },
                          tdClass: 'fonteLinhas'
                      },
                      {
                          key: 'dataAssinatura',
                          label:'Data Assinatura',
                          sortable: true,
                          formatter: 'formatarData',
                          thStyle: { width: "10%",  },
                          tdClass: 'fonteLinhas'
                      },
                       {
                        key: 'opcao',
                        label:'Opções',
                        sortable: true
                      }
                      
          ],
         
            formdata:{
               exercicio: 2021,
               exercicios: [
                            { value: '2021', text: '2021' }
                           ],
               remessa: 1,
               remessas: [
                            { value: '1', text: '1' },
                            { value: '2', text: '2' },
                            { value: '3', text: '3' },
                            { value: '4', text: '4' },
                            { value: '5', text: '5' },
                            { value: '6', text: '6' },
                            { value: '7', text: '7' },
                            { value: '8', text: '8' },
                            { value: '9', text: '9' },
                            { value: '10', text: '10' }
                          ]
            },
          }
        },
       
       mounted(){
            this.ActionFind(),
            
             setTimeout(() =>{// aguarda com spinner antes da pesquisa aparecer na pesquisa inicial
                  this.isBusy = false
                  }, 2.0*2000)
        },
      
        computed:{
                  rows() {
                  return this.tableData.length
                  },
                  ...mapState('remessas', ['tableData'])
        },
        methods: {
                   ...mapActions('remessas', ['ActionFind']),
                   ...mapActions('remessas', ['ActionFindByRemessa']),
              

                async pesquisarRemesssa() {
                                 this.isBusy = !this.isBusy //loading
                                 await this.ActionFindByRemessa(this.formdata)
                                 this.isBusy = false
                },
                  mascaraCnpj(value) {
                            var mascara = (`${value}`).replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, "$1.$2.$3/$4-$5")
                            return mascara;
                  },
                  formatarData: function (value) {
                       if (value === null) { return null }
                      return new Date(value).toLocaleString('pt-BR', { year: 'numeric', month: '2-digit', day: '2-digit', hour:'2-digit', minute:'2-digit', second:'2-digit' })
                  },
                  
                  info(item, index, button) {

                          this.$root.$emit('bv::show::modal', this.infoModal.id, button)
                          this.infoModal.title = `Unidade Gestora: ${item.nomeEntidade}`
                          this.buscarDadoRemessa(item.chave)

                    },


                     resetInfoModal() {
                                      this.infoModal.title = ''
                                      this.infoModal.content = ''
                                      this.gestor= "",
                                      this.rh= "",
                                      this.controleInterno= "",
                                      this.dataAssinaturaGestor= "",
                                      this.dataAssinaturaRh= "",
                                      this.dataAssinaturaCI= ""
                                    },

                   //   abrirRecibo(ug, chave, remessa, exercicio) {
                     abrirRecibo(item) {
                                 window.open("http://relatorios.tce-to.tce.to.gov.br/Relatorios/Pages/ReportViewer.aspx?%2fSicapAP2021%2fReciboRemessa&rs:Command=Render" + "&ug=" + item.cnpj + "&remessa=" + item.remessa + "&exercicio=" + item.exercicio + "&rs:Format=PDF");
                     },





 buscarDadoRemessa (chave) {

    this.findGestor(chave);
    this.findRh(chave);
    this.findControleInterno(chave);


  },

 async findGestor(chave){

    const data =  await this.getResponsavel('Gestor', chave);

    if (data !== 'semPermissao') {
      this.gestor = data[0];
      this.dataAssinaturaGestor = data[3]
      this.statusGestor = this.dataAssinaturaGestor !== null ? 'Assinado' : 'Não Assinado';
    } else {
      this.statusGestor = 'Sem Permissão';
    }
  },

 async findRh (chave) {
    const data =  await this.getResponsavel('Responsável R.H.', chave);
    if (data !== 'semPermissao') {
      this.rh = data[0];
      this.dataAssinaturaRh = data[3];
      this.statusRh = this.dataAssinaturaRh !== null ? 'Assinado' : 'Não Assinado';
    } else {
      this.statusRh = 'Sem Permissão';
    }
  },

 async findControleInterno(chave)  {
    const data = await  this.getResponsavel('Controle Interno', chave);
    if (data !== 'semPermissao') {
      this.controleInterno = data[0];
      this.dataAssinaturaCI = data[3];
      this.statusCI = this.dataAssinaturaCI !== null ? 'Assinado' : 'Não Assinado';
    } else {
      this.statusCI = data;
    }
  },
   async getResponsavel(cargo,chave) {

    const response = await api.get("externo/acompanhamentoRemessa/" + cargo +"/" +chave)
    return response.data;

  }




          }
  }
</script>
<style >
.fonteLinhas {
   font-size:14px;
}

</style>