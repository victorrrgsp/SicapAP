<template>
   <div>
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
                    <b-button :disabled="!filter" @click="filter = ''">Limpar</b-button>
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

:hidden="verIcon(data.item)"  
  <!-- @hidden="verIcon(data.item)" -->
          <template #cell(opcao)="data">
            <b-icon 
                :hidden="verIcon(data.item)"    
                :icon="verIcon2(data.item)" 
                cursor= "pointer" 
                title="Assinaturas"
                @click="info(data.item, data.index, $event.target)" pill 
                variant="primary" 
                size="sm">
            </b-icon>
         &nbsp;   
            <b-icon icon="file-earmark-arrow-down" 
                :hidden="verIcon(data.item)"  
                cursor= "pointer" 
                title="Assinaturas"
                @click="abrirRecibo(data.item)" pill 
                variant="primary" 
                size="sm">
            </b-icon>
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
import jsPDF from 'jspdf'

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


                    verIcon(item){

                          if(item.chave){

                                return false
                                       
                          }

                          else{
                            return true
                             
                          }
                    },
                
                
                
                verIcon2(item){

                          if(item.chave){
                                       return "pen-fill"
                          }

                          else{
                               return "pencil"
                          }
                    },



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

                  abrirRecibo(item) {

                                        let pdfName = 'Recibo Sicap AP '+item.cnpj+'_'+item.exercicio+'_'+item.remessa; 
                                        var doc = new jsPDF();
                                        var imgData = 'data:image/jpeg;base64,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'
                                       
                                        doc.addImage(imgData, 'JPEG', 10, 5, 20, 25);
                                        doc.setFontSize(12);
                                        doc.text("TRIBUNAL DE CONTAS DO ESTADO DO TOCANTINS", 40, 10);
                                        doc.text("Sistema Integrado de Controle e Auditoria Pública", 40, 17);
                                        doc.text("Atos de Pessoal", 40, 23);

                                        doc.setFont(undefined, 'bold')
                                        doc.text("Recibo de Entrega", 70, 50).setFontSize(8).setFont(undefined, 'bold');

                                        doc.setFontSize(10);
                                        doc.setFont(undefined, 'bold')
                                        doc.text("Unidade Gestora: ", 10, 60);
                                        doc.setFont(undefined, 'normal')
                                        doc.text(" "+item.nomeEntidade, 40, 60);
                                        
                                        doc.setFont(undefined, 'bold')
                                        doc.text("CNPJ: ", 10, 70);

                                        doc.setFont(undefined, 'normal')
                                        doc.text(""+item.cnpj, 25, 70);

                                        doc.setFont(undefined, 'bold')
                                        doc.text("Código de Entrega: ", 10, 80);

                                        doc.setFont(undefined, 'normal')
                                        doc.text(""+item.chave, 45, 80);

                                        doc.setFont(undefined, 'bold')
                                        doc.text("Data Entrega: ", 10, 90);

                                        doc.setFont(undefined, 'normal')
                                        doc.text(""+this.formatarData(item.dataEntrega)+"", 35, 90);

                                        doc.setFont(undefined, 'bold')
                                        doc.text("Última Asinatura: ", 10, 100);

                                        doc.setFont(undefined, 'normal')
                                        doc.text(""+this.formatarData(item.dataAssinatura)+"", 40, 100);

                                        doc.text("O Tribunal de Contas do Tocantins, atesta o recebimento das informações referentes aos dados do(s) mês(es) " +item.remessa+ " de "+item.exercicio, 10, 120);  

                                        doc.save(pdfName + '.pdf');

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