<template>
  <div>
    <b-card no-body>
      <b-card-header header-tag="nav">
        <b-nav card-header tabs>
          <b-nav-item active>Remessa</b-nav-item>
        </b-nav>
      </b-card-header>
      <b-row>
        &nbsp;&nbsp;&nbsp;
        <b-col>
          <p align="left">
            <b-form-group class="label-spacing" label="Unidade Gestora:">
              <b-form-input style="height: auto; margin-right: 10x;" list="unidadeGestora" required v-model="filterform" name="unidadeGestora"
                placeholder="Pesquise aqui...">
              </b-form-input>

              <b-form-datalist id="unidadeGestora">
                <option v-for="(item, index) in unidades" v-bind:key="index.id">
                  {{ item.nome }}
                </option>
              </b-form-datalist>
            </b-form-group>
          </p>
        </b-col>
        <b-col cols="auto">
          <p align="left">
            <b-row>
              <b-col cols="auto">
                <b-form-group class="label-spacing" style="margin-left: 10px;" label="Status:">
                  <!-- @charge="filtraStatus"-->
                  <b-form-select style="height: auto;" class="select-selected" v-model="formdata.statu" :options="formdata.status">
                  </b-form-select>
                </b-form-group>
              </b-col>
              <b-col cols="auto">
                <b-form-group class="label-spacing" style="margin-left: 10px;" label="Exercício:">
                  <!-- @charge="filtraStatus"-->
                  <b-form-select style="height: auto;" class="select-selected " v-model="formdata.exercicio" :options="formdata.exercicios">
                  </b-form-select>
                </b-form-group>
              </b-col>
              <b-col cols="auto" >
                <b-form-group style="height: auto; margin-left: 10px;" class="label-spacing"  label="Remessa:">
                  <!-- @charge="filtraStatus"-->
                  <b-form-select class="select-selected spacing" v-model="formdata.remessa" :options="formdata.remessas">
                  </b-form-select>
                </b-form-group>
              </b-col>
               &nbsp;
            </b-row>
          </p>
          <p align="right" class="label-spacing">
            <b-button pill variant="warning spacing" @click="resetFilter" size="sm">
              Limpar
            </b-button>
            <b-button @click="consultarRemessa" pill variant="success" size="sm" >
              Pesquisar
            </b-button>
            &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
          </p>
        </b-col>
      </b-row>
      
      <b-table striped hover responsive sticky-header="450px" id="my-table" :busy="isBusy" :items="tableData2"
        :filter="filter" :fields="columns" :filter-included-fields="['nomeEntidade']" :current-page="currentPage"
        aria-controls="my-table" small>
        <template #table-busy>
          <div class="text-center text-danger my-2" >
            <b-spinner class="align-middle"></b-spinner>
            <strong>Loading...</strong>
          </div>
        </template>

        <!--  Assinaturas  -->
        <template #cell(assinaturas)="data">
          <b-icon icon="pen-fill" :hidden="verIconAssinatura(data.item)" cursor="pointer" title="Assinaturas"
            @click="info(data.item, data.index, $event.target)" pill variant="primary" size="sm">
          </b-icon>
          &nbsp;
          <b-icon icon="file-earmark-arrow-down" :hidden="verIconRecibo(data.item)" cursor="pointer" title="Recibo"
            @click="abrirRecibo(data.item)" pill variant="primary" size="sm">
          </b-icon>
        </template>
        <template #cell(status)="data">
          <b-icon class="icons" :icon="iconStatus(data.item)" cursor="pointer" :title="iconStatusTitle(data.item)"
            :variant="iconStatusVariant(data.item)" size="sm">
          </b-icon>
        </template>
      </b-table>
      <b-row>
        <b-col>
          <div v-show="FilterSize < 1 && isBusy==false" class=" text-center font-weight-bold" style="font-size: 1.3em;"> 
            <strong>
              não contém registros
            </strong>
          </div>
        </b-col>
        <b-col cols="auto">
          <div class="align-right font-weight-bold spacing" style="font-size: 1.3em;">
            <strong>
              {{ FilterSize }} Registro(s)
            </strong>
          </div>
        </b-col>
      </b-row>
    </b-card>
    <b-row>
      <b-col cols="auto">
        <div>
          <b-icon class="h6 mb-0 icons" icon="check-square" variant="success"></b-icon>
          &nbsp; &nbsp;Assinado &nbsp; &nbsp; &nbsp;
        </div>
      </b-col>
      <b-col cols="auto">
        <div>
          <b-icon class="h6 mb-0 icons" icon="check-circle-fill" variant="warning"></b-icon>
          &nbsp; &nbsp;Assinado Intempestivamente &nbsp; &nbsp; &nbsp;
        </div>
      </b-col>
      <b-col cols="auto">
        <div>
          <b-icon class="h6 mb-0 icons" icon="key-fill" variant="danger"></b-icon>
          &nbsp; &nbsp;Pendente de Assinatura &nbsp; &nbsp; &nbsp;
        </div>
      </b-col>
      <b-col cols="auto">
        <div>
          <b-icon class="h6 mb-0 icons" icon="question-circle-fill" variant="danger"></b-icon> 
          &nbsp; &nbsp;Aguardando Envio &nbsp; &nbsp;
        </div>
      </b-col>
    </b-row>

    <!-- Info modal -->
    <b-modal :id="infoModal.id" :title="infoModal.title" ok-only @hide="resetInfoModal" size="xl">
      <b-container fluid="xl">
        <b-row>
          <b-col><b>Gestor</b></b-col>
          <b-col><b>Responsável R.H.</b></b-col>
          <b-col><b>Controle Interno</b></b-col>
        </b-row>
        <b-row class="fonteLinhas">
          <b-col>{{ gestor }}</b-col>
          <b-col>{{ rh }}</b-col>
          <b-col>{{ controleInterno }}</b-col>
        </b-row>
        <b-row class="fonteLinhas">
          <b-col>Data Assinatura:
            {{
                dataAssinaturaGestor == ""
                  ? "---"
                  : formatarData(dataAssinaturaGestor)
              }}</b-col>
          <b-col>Data Assinatura:
            {{
                  dataAssinaturaRh === "" ? "---" : formatarData(dataAssinaturaRh)
                }}</b-col>
          <b-col>Data Assinatura:
            {{
                  dataAssinaturaCI === "" ? "---" : formatarData(dataAssinaturaCI)
                }}</b-col>
        </b-row>
      </b-container>
    </b-modal>
  </div>
</template>

<script>
import { api } from "@/plugins/axios";
import jsPDF from "jspdf";

import { mapActions } from "vuex";
import maskMixins from "@/helpers/mixins/mask";
export default {
  mixins: [maskMixins],

  data() {
    return {
      unidades: [],
      tableData:'',
      statu:'',
      status:'',
      once: false,
      tableData2: [],
      isBusy: true,
      perPage: 325,
      currentPage: 1,
      filter: "",
      FilterSize: '',
      filterform: "",
      items: [],
      gestor: "",
      rh: "",
      controleInterno: "",
      dataAssinaturaGestor: "",
      dataAssinaturaRh: "",
      dataAssinaturaCI: "",
      infoModal: {
        id: "info-modal",
        title: "",
        content: "",
      },
      columns: [
        {
          key: "nomeEntidade",
          label: "Unidade Gestora",
          sortable: false,
          thStyle: { width: "38%" },
          tdClass: "fonteLinhasLeft",
          // class: "borderGrid"
        },
        {
          key: "cnpj",
          label: "CNPJ",
          sortable: true,
          thStyle: { width: "10%" },
          formatter: "mascaraCnpj",
          tdClass: "fonteLinhasLeft",
        },
        {
          key: "exercicio",
          label: "Exercício",
          sortable: false,
          thStyle: { width: "1%", textAlign: "center"},
          tdClass: "fonteLinhas",
        },
        {
          key: "remessa",
          label: "Remessa",
          thStyle: { width: "1%", textAlign: "center"},
          sortable: false,
          tdClass: "fonteLinhas",
        },
        {
          key: "relatoria",
          label: "Relatoria",
          thStyle: { width: "1%", textAlign: "center"},
          sortable: false,
          tdClass: "fonteLinhas",
        },
        {
          key: "dataEntrega",
          label: "Data Entrega",
          sortable: true,
          formatter: "formatarData",
          thStyle: { width: "10%" , textAlign: "center"},
          tdClass: "fonteLinhas",
        },
        {
          key: "dataAssinatura",
          label: "Data Assinatura",
          sortable: true,
          formatter: "formatarData",
          thStyle: { width: "10%" , textAlign: "center"},
          tdClass: "fonteLinhas",
        },
        {
          key: "contAssinaturas",
          label: "Quant. Assinaturas",
          thStyle: { width: "8%" , textAlign: "center"},
          tdClass: "fonteLinhas",
          sortable: true,
        },
        {
          key: "situacaoGFIP",
          label: "Situção GFIP",
          thStyle: { width: "6%", textAlign: "center"},
          tdClass: "fonteLinhas",
          sortable: false,
        },
        {
          key: "assinaturas",
          label: "Assinaturas",
          tdClass: "fonteLinhas",
          thStyle: { width: "1%", textAlign: "center"},
          sortable: false,
        },
        {
          key: "status",
          label: "Status",
          thStyle: { width: "1%", textAlign: "center"},
          tdClass: "fonteLinhas",
          sortable: false,
        },
      ],
      formdata: {
        status: [
          "Todos",
          "Todos Enviados",
          "Todos Assinados",
          "Assinado Intempestivamente",
          "Pendente de assinatura",
          "Aguardando Envio"
        ],
        statu: "Todos",
        exercicio: '',
        exercicios: [],
        remessa: '',
        remessas: [],
      },
    };
  },

  mounted() {
    this.isBusy = false;
    this.pesquisarExercicios();
    this.pesquisarExercicioVigente();
    // this.consultarRemessa();
    this.findAllUnidadeGestora().then(resp => {
      this.unidades = resp;
    });
    this.status = this.formdata.status;
    this.statu = this.formdata.statu;
  },
  computed: {
    rows() {
      return this.tableData2.length;
    },

    filteredUnidades() {
      const normalizedFilter = this.normalizeText(this.filterform);

      return this.unidades.filter(item =>
        this.normalizeText(item.nome).includes(normalizedFilter)
      );
    },
  },
  methods: {

    ...mapActions("remessas", ["ActionFind"]),
    ...mapActions("remessas", ["ActionFindByRemessa"]),
    ...mapActions("remessas", ["ActionFindByExercicio"]),
    ...mapActions("remessas", ["ActionFindExercicio"]),
    ...mapActions("remessas", ["ActionFindByExercicioVigente"]),
    ...mapActions("remessas", ["ActionFindByRemessaVigente"]),
    ...mapActions("remessas", ["ActionSituacaoGFIP"]),

    filtroInicial(){
      // this.filterSize();
      this.tableData2 = [];
      this.formdata.exercicio = this.pesquisarExercicioVigente();
      this.formdata.exercicios = this.pesquisarExercicios();
      this.filterform = "";
      this.filter = "";
    },

    resetFilter(){
      this.formdata = { ...this.filtroInicial()}
    },

    statusteste(){
      this.formdata.status = this.status;
      return this.formdata.status;
    },
    statuteste(){ 
      this.formdata.statu = this.statu;
      return this.formdata.statu;
    },

    normalizeText(text) {
      return text
        ? text.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
        : '';
    }, 
    pesquisarExercicioVigente() { // vai buscar o exercicio de está em vigencia
      api.get("/exercicio/exercicioVigente").then((resp) => {
        this.formdata.exercicio = resp.data[0];
        this.pesquisarRemessas(resp.data[0]);
        this.pesquisarRemessaVigente(resp.data[0]);
        this.statusteste();
        this.statuteste();
      });
    },
    pesquisarRemessaVigente(exercicio) { // vai buscar o remessa de está em vigencia
        api.get("/remessa/remessaVigente/" + exercicio).then((resp) => {
        this.formdata.remessa = resp.data[0];
        this.consultarRemessa();
      });
    },
    pesquisarExercicios() {
      api.get("/exercicio").then((resp) => {
        this.formdata.exercicios = resp.data
          .filter(p => p > 2020)
          .map((p) => {
            return {
              value: p,
              text: "" + p,
            };
          });
      });
    },
    pesquisarRemessas(exercicio) {
      api.get("/remessa/" + exercicio).then((resp) => {
        this.formdata.remessas = resp.data.map((p) => {
          return {
            value: p,
            text: "" + p,
          };
        });
        this.formdata.remessas[this.formdata.remessas.length] = {
          value: 0,
          text: "Todos",
        };
      });
    },
    filtraStatus(status) {
      status = this.formdata.statu;
      this.tableData2 = this.tableData2.filter(function (x) {
        let retorno = false
        switch (status) {  
          case "Todos":
            retorno = true
            break
          case "Todos Assinados":
            if (x.contAssinaturas > 2) {
              retorno = true
            }
            break;
          case "Assinado Intempestivamente":
            if (x.dataAssinatura > x.dataFinalEnvio) {
              retorno = true
            }
            break;
          case "Pendente de assinatura":
            if (x.contAssinaturas >= 0 && x.contAssinaturas <= 2 && x.dataEntrega != null) {
              retorno = true
            }
            break; 
          case "Aguardando Envio":
            if (x.dataEntrega === null) {
              retorno = true
            }
            break;
          case "Todos Enviados":
            if (x.dataEntrega !== null) {
              retorno = true
            }
            break;
          default:
            retorno = false
            break;
        }
        return retorno
      });
    },
    iconStatusTitle(item) {
      if (item.dataAssinatura > item.dataFinalEnvio) {
        return "Assinado Intempestivamente";
      }
      if (item.contAssinaturas > 2 && item.dataAssinatura < item.dataFinalEnvio) {
        return "Todos Assinados";
      }
      if (item.dataEntrega == null ) {
        return "Aguardando Envio";
      }
      if (item.contAssinaturas >= 0 && item.contAssinaturas <= 2) {
        return "Pendente de Assinatura";
      }
    },
    filterSize() {
      let sum = 0;
      this.tableData2.map(x => {
        if (x.nomeEntidade.toUpperCase().includes(this.filter.trim().toUpperCase())) {
          sum++;
        }
      });
      this.FilterSize = sum;
      return sum;
    },
    iconStatus(item) {
      switch (this.iconStatusTitle(item)) {
        case "Todos Assinados":
          return "check-square";
        case "Assinado Intempestivamente":
          return "check-circle-fill";
        case "Pendente de Assinatura":
          return "key-fill";
        case "Aguardando Envio":
          return "question-circle-fill";
      }
    },
    iconStatusVariant(item) {
      if (item.dataAssinatura > item.dataFinalEnvio) {
        return "warning";
      }
      if (item.contAssinaturas > 2) {
        return "success";
      }
      
      if (
        (item.contAssinaturas >= 1 && item.contAssinaturas <= 2) |
        (item.qntDocumentoGFIP < 3)
      ) {
        return "danger";
      } else {
        return "danger";
      }
    },
    verIconAssinatura(item) {
      if (item.contAssinaturas >= 1) {
        return false;
      } else {
        return true;
      }
    },
    verIconRecibo(item) {
      if (item.contAssinaturas > 2) {
        return false;
      } else {
        return true;
      }
    },
    consultarRemessa(){
      this.isBusy = !this.isBusy;
      let exercicio = this.formdata.exercicio;
      let remessa = this.formdata.remessa;
      // console.log('passou aki')
      api.get("/externo/acompanhamentoRemessa/getExercicio/" + exercicio + "/" + remessa ).then((resp) => {
        
        this.tableData2 = resp.data;
        this.filtraStatus();
        this.filterSize();
        this.isBusy = false;
      });
      this.filter = this.filterform;
    },
    async pesquisar() {
      this.isBusy = !this.isBusy; //loading
      await this.ActionFind();
      this.isBusy = false;
      this.filterSize();
    },
    mascaraCnpj(value) {
      var mascara = `${value}`.replace(
        /^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/,
        "$1.$2.$3/$4-$5"
      );
      return mascara;
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
    info(item, index, button) {
      this.$root.$emit("bv::show::modal", this.infoModal.id, button);
      this.infoModal.title = `Unidade Gestora: ${item.nomeEntidade}`;
      this.buscarDadoRemessa(item.chave);
    },
    resetInfoModal() {
      this.infoModal.title = "";
      this.infoModal.content = "";
      (this.gestor = ""),
        (this.rh = ""),
        (this.controleInterno = ""),
        (this.dataAssinaturaGestor = ""),
        (this.dataAssinaturaRh = ""),
        (this.dataAssinaturaCI = "");
    },
    abrirRecibo(item) {
      //let pdfName = 'Recibo Sicap AP '+item.cnpj+'_'+item.exercicio+'_'+item.remessa;
      var doc = new jsPDF();
      var imgData =
        "data:image/jpeg;base64,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";

      doc.addImage(imgData, "JPEG", 10, 5, 20, 25);
      doc.setFontSize(12);
      doc.text("TRIBUNAL DE CONTAS DO ESTADO DO TOCANTINS", 40, 10);
      doc.text("Sistema Integrado de Controle e Auditoria Pública", 40, 17);
      doc.text("Atos de Pessoal", 40, 23);

      doc.setFont(undefined, "bold");
      doc
        .text("Recibo de Entrega", 70, 50)
        .setFontSize(8)
        .setFont(undefined, "bold");

      doc.setFontSize(10);
      doc.setFont(undefined, "bold");
      doc.text("Unidade Gestora: ", 10, 60);
      doc.setFont(undefined, "normal");
      doc.text(" " + item.nomeEntidade, 40, 60);

      doc.setFont(undefined, "bold");
      doc.text("CNPJ: ", 10, 70);

      doc.setFont(undefined, "normal");
      doc.text("" + this.mascaraCnpj(item.cnpj), 25, 70);

      doc.setFont(undefined, "bold");
      doc.text("Código de Entrega: ", 10, 80);

      doc.setFont(undefined, "normal");
      doc.text("" + item.chave, 45, 80);

      doc.setFont(undefined, "bold");
      doc.text("Data Entrega: ", 10, 90);

      doc.setFont(undefined, "normal");
      doc.text("" + this.formatarData(item.dataEntrega) + "", 35, 90);

      doc.setFont(undefined, "bold");
      doc.text("Última Assinatura: ", 10, 100);

      doc.setFont(undefined, "normal");
      doc.text("" + this.formatarData(item.dataAssinatura) + "", 41, 100);

      doc.text(
        "O Tribunal de Contas do Tocantins, atesta o recebimento das informações referentes aos dados do(s) mês(es) " +
        item.remessa +
        " de " +
        item.exercicio,
        10,
        120
      );

      window.open(doc.output("bloburl"), "_blank");
    },

    findAllUnidadeGestora: () =>
      api.get("/unidadeGestora/todos").then((resp) => {
        //commit('getUnidades', resp.data)
        return resp.data;
      }),

    buscarDadoRemessa(chave) {
      this.findGestor(chave);
      this.findRh(chave);
      this.findControleInterno(chave);
    },
    async findGestor(chave) {
      const data = await this.getResponsavel("Gestor", chave);

      if (data !== "semPermissao") {
        this.gestor = data[0];
        this.dataAssinaturaGestor = data[3];
        this.statusGestor =
          this.dataAssinaturaGestor !== null ? "Assinado" : "Não Assinado";
      } else {
        this.statusGestor = "Sem Permissão";
      }
    },
    async findRh(chave) {
      const data = await this.getResponsavel("Responsável R.H.", chave);
      if (data !== "semPermissao") {
        this.rh = data[0];
        this.dataAssinaturaRh = data[3];
        this.statusRh =
          this.dataAssinaturaRh !== null ? "Assinado" : "Não Assinado";
      } else {
        this.statusRh = "Sem Permissão";
      }
    },

    async findControleInterno(chave) {
      const data = await this.getResponsavel("Controle Interno", chave);
      if (data !== "semPermissao") {
        this.controleInterno = data[0];
        this.dataAssinaturaCI = data[3];
        this.statusCI =
          this.dataAssinaturaCI !== null ? "Assinado" : "Não Assinado";
      } else {
        this.statusCI = data;
      }
    },

    async getResponsavel(cargo, chave) {
      const response = await api.get(
        "externo/acompanhamentoRemessa/" + cargo + "/" + chave
      );
      return response.data;
    },
  },
};
</script>
<style>
.fonteLinhas {
  font-size: 14px;
  text-align: center;
}

.fonteLinhasLeft {
  font-size: 14px;
}

.icons {
  font-size: 18px;
}

.label-spacing{
  margin-top: 10px;
}

.align-right{
  text-align: right;
}

.spacing {
  margin-right: 20px;
}

.width-unidade {
  width: 810px; 
  overflow: hidden; 
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

.pesquisa_select {
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