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
     <b-table striped hover responsive  
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

        

         <template #cell(index)="data">{{ data.index + 1 }}</template>
     </b-table>
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
import maskMixins from '@/helpers/mixins/mask'
export default {

        mixins:[maskMixins], 

        data () {
          return {
            isBusy: true,
            perPage: 20,
            currentPage: 1,
            filter: null,
            items:[],
            columns:[  
                      
                      {
                        key:'index',
                        label:'Indice',
                        formatter: 'index'
                      },
                      {
                        key: 'nomeEntidade',
                        label:'Unidade Gestora',
                        sortable: true
                       // formatter: 'todasMaiusculas'
                      },
                       {
                        key: 'cnpj',
                         label:'Cnpj',
                         sortable: false,
                         thStyle: { width: "10%" },
                         formatter: 'mascaraCnpj'
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
                         key: 'relatoria',
                         label:'Relatoria',
                         sortable: false
                      },
                      {
                        key:'contAssinaturas',
                        label:'Assinaturas',
                        sortable: true,
                      //  formatter: 'index'
                      },
                      {
                          key: 'dataEntrega',
                          label:'Data Entrega',
                          sortable: false,
                          formatter: 'formatarData'
                      },
                      {
                          key: 'dataAssinatura',
                          label:'Data Assinatura',
                          sortable: true,
                          formatter: 'formatarData'
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
                  // rowClass(item, type) {

                  //     var icon = ""

                  //    if (!item || type !== 'row') return
                  //    (item.contAssinaturas == 3) ? icon= item.contAssinaturas='door-open' : icon=item.contAssinaturas='trash'

                  //     return icon

                  //  }
          }
  }
</script>
<style >

</style>